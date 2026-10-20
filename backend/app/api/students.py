from fastapi import APIRouter
from typing import Optional

from app.models import Student, StudentUpdate
from app.crud import *

router = APIRouter(tags=["students"], prefix="/students")

@router.get("")
async def read_root(
        group: Optional[str] = None,
        name: Optional[str] = None,
        isu: Optional[int] = None,
        dormitory: Optional[int] = None,
        room: Optional[int] = None,
        check_in: Optional[date] = None,
        is_foreigner: Optional[bool] = None,
        notes: Optional[str] = None
        ):
    students = await get_all_students()

    if not (group or name or isu or dormitory or room or check_in or notes or is_foreigner is not None):
        return students

    list_students = await check_student(group, name, isu, dormitory, room, check_in, notes, is_foreigner, students)

    return list_students

@router.get("/{isu}")
async def read_student(isu: int):
    return await get_student(isu)

@router.post("")
async def post_student(student: Student):
    return await create_student(student)

@router.put("/{isu}")
async def upd_student(isu: int, student: StudentUpdate):
    return await update_student(isu, student)

@router.patch("/{isu}")
async def patch_student(isu: int, student: StudentUpdate):
    return await update_student(isu, student)

@router.delete("/{isu}")
async def del_student(isu: int):
    return await delete_student(isu)
