from datetime import date
from typing import Optional

from pydantic import BaseModel, Field, StrictBool, StrictInt, field_validator


class Student(BaseModel):

    isu: StrictInt = Field(ge=100000, le=999999, description="ИСУ ID")
    name: str = Field(min_length=2, description="ФИО студента")
    group: str = Field(
        min_length=1,
        pattern=r"[A-Z][1-9][1-4]\d{2}",
        description="Номер группы, например M3301",
    )
    dormitory: int = Field(ge=1, le=99, description="Номер общежития, например 8")
    room: StrictInt = Field(ge=1, le=999, description="Номер комнаты")
    check_in: date = Field(description="Срок заселения")
    is_foreign: StrictBool = Field(
        default=False, description="Является ли студент иностранцем"
    )
    notes: str = Field(default="", description="Заметки")

    @field_validator("name")
    @classmethod
    def validate_name(cls, value: str) -> str:
        value = value.strip()
        parts = value.split()

        if len(parts) < 2:
            raise ValueError("ФИО должно содержать минимум 2 отдельных слова")

        if any(len(part) < 2 for part in parts):
            raise ValueError("Длина каждой части ФИО должна быть не менее 2 символов")

        if not all(i.isalpha() for i in parts):
            raise ValueError("В имени могут быть только буквы")

        return value


class StudentUpdate(BaseModel):

    name: Optional[str] = Field(default=None, min_length=2)
    group: Optional[str] = Field(
        default=None, min_length=1, pattern=r"[A-Z][1-9][1-4]\d{2}"
    )
    dormitory: Optional[int] = Field(default=None, ge=1, le=99)
    room: Optional[StrictInt] = Field(default=None, ge=1)
    check_in: Optional[date] = None
    is_foreign: Optional[StrictBool] = None
    notes: Optional[str] = None

    @field_validator("name")
    @classmethod
    def validate_name(cls, value: Optional[str]) -> Optional[str]:
        if value is None:
            return value
        value = value.strip()
        parts = value.split()

        if len(parts) < 2:
            raise ValueError("ФИО должно содержать минимум 2 отдельных слова")

        if any(len(part) < 2 for part in parts):
            raise ValueError("Длина каждой части ФИО должна быть не менее 2 символов")

        if not all(i.isalpha() for i in parts):
            raise ValueError("В имени могут быть только буквы")

        return value
