from app.core.db import engine
from app.models import Student


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
    data = engine.get_data()
    await collection_exist("students")

    if data["students"].get(str(student.isu)):
        raise KeyError(f"Key {student.isu} already exist!")

    student_dict = student.model_dump()
    pk = str(student_dict.pop("isu"))
    data["students"][pk] = student_dict

    engine.set_dirty(True)
    await engine.write(data)


async def get_student(isu: int) -> Student | None:
    await engine.load()
    data = engine.get_data()
    student_dict = data.get("students", {}).get(str(isu))
    if student_dict is None:
        return None
    student_dict["isu"] = isu
    return Student(**student_dict)


async def update_student(isu: int, new_student: Student | dict) -> Student | None:
    await engine.load()
    data = engine.get_data()
    current_student = data.get("students", {}).get(isu)
    if current_student is None:
        return None

    if isinstance(new_student, Student):
        new_student_dict = new_student.model_dump()
    else:
        new_student_dict = new_student

    merged = {**current_student, **new_student_dict}

    student = Student(**merged)
    student_dict = student.model_dump()
    new_isu = student_dict.pop("isu")
    if new_isu != isu:
        await delete_student(isu)
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
        items.append(Student(**item_data))

    return items


async def student_exists(isu: int) -> bool:
    await engine.load()
    data = engine.get_data()
    return str(isu) in data.get("students", {})
