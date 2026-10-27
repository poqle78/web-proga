from fastapi import APIRouter, HTTPException

from app.models import Student, StudentUpdate, StudentFilter
from app.crud import *

router = APIRouter(tags=["students"], prefix="/students")


@router.get("")
async def read_root():
    students = await get_all_students()
    return students


@router.api_route("", methods=["QUERY"])
async def query_students(filters: StudentFilter):
    students = await get_all_students()
    return await check_student(
        students,
        filters
    )


@router.get("/{isu}")
async def read_student(isu: int):
    student = await get_student(isu)
    if student:
        return student
    raise HTTPException(status_code=404, detail="Студента с таким ИСУ нет!")


@router.post("", status_code=201)
async def post_student(student: Student):
    try:
        await create_student(student)
        return student
    except KeyError:
        raise HTTPException(status_code=409, detail="Студент с таким ИСУ уже есть!")


@router.put("/{isu}")
async def upd_student(isu: int, student: StudentUpdate):
    new_student = await update_student(isu, student)
    if new_student is None:
        raise HTTPException(status_code=404, detail="Студента с таким ИСУ нет!")
    return new_student


@router.patch("/{isu}")
async def patch_student(isu: int, student: StudentUpdate):
    new_student = await update_student(isu, student)
    if new_student is None:
        raise HTTPException(status_code=404, detail="Студента с таким ИСУ нет!")
    return new_student


@router.delete("/{isu}", status_code=204)
async def del_student(isu: int):
    result = await delete_student(isu)
    if not result:
        raise HTTPException(status_code=404, detail="Студента с таким ИСУ нет!")
