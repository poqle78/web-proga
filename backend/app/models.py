from pydantic import BaseModel, Field, StrictInt, field_validator




class Student(BaseModel):
    isu: StrictInt = Field(ge=100000, le=999999)
    name: str

    @field_validator("name")
    def validate_name(self, value: str):
        name_parts = value.split()
        if len(name_parts) < 2:
            raise ValueError("ФИО должно содержать минимум 2 отдельных слова")
        elif any(len(i) < 2 for i in name_parts):
            raise ValueError("Длина каждого должна составлять не менее 2 символов")
        elif value.isalpha():
            raise ValueError("В имени не должно быть цифр")
        return value
