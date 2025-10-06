from typing import List
from enum import Enum
from pydantic import BaseModel, Field


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
    id: str = Field(..., alias="subject_id")
    teacher_id : str 
    hours: int
    max_hours_per_day: int
    name: str = Field(..., alias="subject_name")
    type: SubjectPriority
    room: RoomPreferences

    model_config = {"from_attributes": True}


class GroupInput(BaseModel):
    id: str = Field(..., alias="group_id")
    name: str = Field(..., alias="group_name")
    conflicting_groups: List[str]
    subjects: List[SubjectInput]

    model_config = {"from_attributes": True}


class UnavailabilityInput(BaseModel):
    day: int
    hour: int

    model_config = {"from_attributes": True}


class TeacherInput(BaseModel):
    id: str = Field(..., alias="teacher_id")
    name: str = Field(..., alias="teacher_name")
    unavailability: List[UnavailabilityInput]

    model_config = {"from_attributes": True}


class Goal(BaseModel):
    name: str
    time: int

    model_config = {"from_attributes": True}


class ScheduleConfig(BaseModel):
    max_hours_per_day: int
    teaching_days: int
    goals: List[Goal]
    rooms: List[str]
    groups: List[GroupInput]
    teachers: List[TeacherInput]

    model_config = {"from_attributes": True}

