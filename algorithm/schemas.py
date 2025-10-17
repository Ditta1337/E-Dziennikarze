from typing import List
from enum import Enum
from pydantic import BaseModel


class SubjectPriority(str, Enum):
    ANY = "ANY"
    EARLY = "EARLY"
    EDGE = "EDGE"
    LATE = "LATE"


class RoomPreferences(BaseModel):
    allowed: List[str] 
    dispreferred: List[str] 
    preferred: List[str] 

    model_config = {"from_attributes": True}


class SubjectInput(BaseModel):
    subject_id: str 
    teacher_id : str 
    lessons_per_week: int
    max_lessons_per_day: int
    type: SubjectPriority
    room: RoomPreferences

    model_config = {"from_attributes": True}


class GroupInput(BaseModel):
    group_id: str
    subjects: List[SubjectInput]

    model_config = {"from_attributes": True}


class UnavailabilityInput(BaseModel):
    day: int
    lesson: int

    model_config = {"from_attributes": True}


class TeacherInput(BaseModel):
    teacher_id: str
    unavailability: List[UnavailabilityInput]

    model_config = {"from_attributes": True}


class Goal(BaseModel):
    name: str
    time: int

    model_config = {"from_attributes": True}


class ScheduleConfig(BaseModel):
    plan_id:str
    lessons_per_day: int
    unique_group_combinations:List[List[str]]
    goals: List[Goal]
    rooms: List[str]
    groups: List[GroupInput]
    teachers: List[TeacherInput]
    latest_starting_lesson: int

    model_config = {"from_attributes": True}

