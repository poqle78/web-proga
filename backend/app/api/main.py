from fastapi import APIRouter
from app.api import students


api_router = APIRouter()
api_router.include_router(students.router)
