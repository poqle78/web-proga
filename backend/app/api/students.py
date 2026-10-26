from fastapi import APIRouter, HTTPException
from typing import Optional
from datetime import date

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
    try:
        return await get_student(isu)
    except Exception:
        raise HTTPException(status_code=404, detail="Студента с таким ИСУ нет!")

@router.post("", status_code=201)
async def post_student(student: Student):
    try:
        return await create_student(student)
    except Exception:
        raise HTTPException(status_code=409, detail="Студент с таким ИСУ уже есть!")

@router.put("/{isu}")
async def upd_student(isu: int, student: StudentUpdate):
    try:
        return await update_student(isu, student)
    except Exception:
        raise HTTPException(status_code=404, detail="Студента с таким ИСУ нет!")

@router.patch("/{isu}")
async def patch_student(isu: int, student: StudentUpdate):
    try:
        return await update_student(isu, student)
    except Exception:
        raise HTTPException(status_code=404, detail="Студента с таким ИСУ нет!")

@router.delete("/{isu}", status_code=204)
async def del_student(isu: int):
    try:
        await delete_student(isu)
    except Exception:
        raise HTTPException(status_code=404, detail="Студента с таким ИСУ нет!")
