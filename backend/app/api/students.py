from fastapi import APIRouter
import typing
from pathfile import Path
from app.models import Student, StudentUpdate
from app.crud import get_student

from backend.app.crud import update_item, update_student

router = APIRouter(tags=["students"], prefix="/students")

@router.get("/")
async def read_root():
    return await get_all_students()

@router.get("/{isu}")
async def read_student(isu: int):
    return await get_student(isu)

@router.post("/")
async def post_student(student: Student):
    return await create_student(student)

@router.put("/{isu}")
async def upd_student(isu: int, student: Student):
    return await update_student(isu, student)

@router.patch("/{isu}")
async def patch_student(isu: int, student: StudentUpdate):
    return await update_student(isu, student)

@router.delete("/{isu}")
async def del_student(isu: int):
    return await delete_student(isu)

@router.query("")


