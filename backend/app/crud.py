import datetime

from app.core.db import engine
from app.models import Student, StudentUpdate


def dump_date(date):
    if isinstance(date, datetime.date):
        return date.strftime('%d-%m-%Y')
    return date

def load_date(date):
    if isinstance(date, str):
        return datetime.datetime.strptime(date, '%d-%m-%Y')
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
    student_dict['check_in'] = dump_date(student_dict['check_in'])
    data["students"][pk] = student_dict
    print(student_dict)
    print(data)
    engine.set_dirty(True)
    await engine.write(data)


async def get_student(isu: int) -> Student | None:
    await engine.load()
    data = engine.get_data()
    student_dict = data.get("students", {}).get(str(isu))
    if student_dict is None:
        return None
    student_dict["isu"] = isu
    student_dict['check_in'] = load_date(student_dict['check_in'])
    return Student(**student_dict)


async def update_student(isu: int, new_student: StudentUpdate | dict) -> Student | None:
    await engine.load()
    data = engine.get_data()
    current_student = data.get("students", {}).get(str(isu))
    if current_student is None:
        return None
    current_student['check_in'] = load_date(current_student['check_in'])

    if isinstance(new_student, StudentUpdate):
        new_student_dict = new_student.model_dump(exclude_unset=True)
    else:
        new_student_dict = new_student

    merged = {**current_student, **new_student_dict}
    merged['isu'] = isu

    student = Student(**merged)
    student_dict = student.model_dump()
    new_isu = student_dict.pop("isu")
    if new_isu != isu:
        await delete_student(isu)
    student_dict['check_in'] = dump_date(student_dict['check_in'])
    data["students"][str(new_isu)] = student_dict
    engine.set_dirty(True)
    await engine.write(data)


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
