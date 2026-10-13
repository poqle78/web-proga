from app.core.db import engine
from app.models import Student


async def collection_exist(name: str) -> None:
    data = engine.get_data()
    if name not in data:
        data[name] = {}
        await engine.write(data)


async def create_student(student: Student) -> None:
    if student.isu is None:
        raise ValueError(f"pk key in Student not exist! item: {student} key: isu")

    data = engine.get_data()
    await collection_exist("students")

    if data["students"].get(student.isu):
        raise KeyError(f"Key {student.isu} already exist!")

    student_dict = student.model_dump()
    pk = student_dict.pop("isu")
    data["students"][pk] = student_dict

    engine.set_dirty(True)
    await engine.write(data)


async def get_student(isu: int) -> Student | None:
    await engine.load()
    data = engine.get_data()
    student_dict = data.get("students", {}).get(isu)
    if student_dict is None:
        return None
    return Student(**student_dict)


async def update_item(isu: int, new_student: Student | dict) -> Student | None:
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

    # Валидируем
    student = Student(**merged)
    student_dict = student.model_dump()
    new_isu = student_dict.pop("isu")
    if new_isu != isu:
        await delete_student(isu)
    data["students"][new_isu] = student.model_dump()
    engine.set_dirty(True)
    await engine.write(data)


async def delete_student(isu: int) -> bool:
    await engine.load()
    data = engine.get_data()

    if isu not in data.get("students", {}):
        return False

    del data["students"][isu]
    engine.set_dirty(True)
    await engine.write(data)

    return True
