import datetime
from typing import Optional


from app.core.db import engine
from app.models import Student, StudentFilter, StudentUpdate


def dump_date(date):
    if isinstance(date, datetime.date):
        return date.strftime("%d-%m-%Y")
    return date


def load_date(date):
    if isinstance(date, str):
        return datetime.datetime.strptime(date, "%d-%m-%Y").date()
    return date


async def collection_exist(name: str) -> None:
    await engine.load()
    data = engine.get_data()
    if name not in data:
        data[name] = {}
        await engine.write(data)


async def create_student(student: Student) -> None:
    if student.isu is None:
        raise ValueError(f"pk key in Student not exist! item: {student} key: isu")

    await engine.load()
    await collection_exist("students")

    data = engine.get_data()

    if data["students"].get(str(student.isu)):
        raise KeyError(f"Key {student.isu} already exist!")

    student_dict = student.model_dump()
    pk = str(student_dict.pop("isu"))
    student_dict["check_in"] = dump_date(student_dict["check_in"])
    data["students"][pk] = student_dict
    engine.set_dirty(True)
    await engine.write(data)


async def get_student(isu: int) -> Student | None:
    await engine.load()
    data = engine.get_data()
    student_dict = data.get("students", {}).get(str(isu))
    if student_dict is None:
        return None
    student_dict = dict(student_dict)
    student_dict["isu"] = isu
    student_dict["check_in"] = load_date(student_dict["check_in"])
    return Student(**student_dict)


async def update_student(isu: int, new_student: StudentUpdate | dict) -> Student | None:
    await engine.load()
    data = engine.get_data()
    current_student = data.get("students", {}).get(str(isu))
    if current_student is None:
        return None
    current_student = dict(current_student)
    current_student["check_in"] = load_date(current_student["check_in"])

    if isinstance(new_student, StudentUpdate):
        new_student_dict = {
            k: v
            for k, v in new_student.model_dump(exclude_unset=True).items()
            if v is not None
        }
    else:
        new_student_dict = new_student

    merged = {**current_student, **new_student_dict}
    merged["isu"] = isu

    student = Student(**merged)
    student_dict = student.model_dump()
    student_dict["check_in"] = dump_date(student_dict["check_in"])

    new_isu = student_dict.pop("isu")
    if new_isu != isu and await student_exists(new_isu):
        raise KeyError("Isu already exist!")

    data["students"][str(new_isu)] = student_dict
    if new_isu != isu:
        del data["students"][str(isu)]
    engine.set_dirty(True)
    await engine.write(data)

    return student


async def delete_student(isu: int) -> bool:
    await engine.load()
    data = engine.get_data()

    if str(isu) not in data.get("students", {}):
        return False

    del data["students"][str(isu)]
    engine.set_dirty(True)
    await engine.write(data)

    return True


async def get_all_students() -> list[Student]:
    await engine.load()
    data = engine.get_data()

    collection = data.get("students", {})
    items = []
    for isu, item_data in collection.items():
        item_data["isu"] = int(isu)
        item_data["check_in"] = load_date(item_data["check_in"])
        items.append(Student(**item_data))

    return items


async def student_exists(isu: int) -> bool:
    await engine.load()
    data = engine.get_data()
    return str(isu) in data.get("students", {})


async def check_student(
    students: list,
    filters: StudentFilter) -> list:
    if (
        filters.group is None
        and filters.name is None
        and filters.isu is None
        and filters.dormitory is None
        and filters.room is None
        and filters.check_in is None
        and filters.is_foreigner is None
        and filters.notes is None
    ):
        return students

    list_students = []

    for student in students:
        flag = False

        if group:
            if student.group == group:
                flag = True
            else:
                flag = False
                continue

        if name:
            if name.lower() in student.name.lower():
                flag = True
            else:
                flag = False
                continue

        if isu:
            if student.isu == isu:
                flag = True
            else:
                flag = False
                continue

        if dormitory:
            if student.dormitory == dormitory:
                flag = True
            else:
                flag = False
                continue

        if room:
            if student.room == room:
                flag = True
            else:
                flag = False
                continue

        if check_in:
            if check_in == student.check_in:
                flag = True
            else:
                flag = False
                continue

        if is_foreigner is not None:
            if is_foreigner == student.is_foreigner:
                flag = True
            else:
                flag = False
                continue

        if notes:
            if student.notes and notes.lower() in student.notes.lower():
                flag = True
            else:
                flag = False
                continue

        if flag:
            list_students.append(student)

    return list_students
