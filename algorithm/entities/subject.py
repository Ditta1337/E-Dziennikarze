from itertools import count
from entities import Room
from schemas import SubjectPriority
from entities import Teacher

class RoomPreference():
    def __init__(self, allowed:list[Room]=[],preferred:list[Room]=[],dispreferred:list[Room]=[]):
        self.allowed = allowed
        self.preferred = preferred
        self.dispreferred = dispreferred



class Subject:
    id_iterator = count()

    def __init__(self, uuid: str, lessons: int, max_lessons_per_day: int, priority: SubjectPriority, teacher: Teacher, room_preference:RoomPreference):
        self._uuid = uuid
        self._teacher = teacher
        self._lessons = lessons
        self._max_lessons_per_day = max_lessons_per_day
        self._priority = priority
        self._id = next(self.id_iterator)
        self._room_preference=room_preference
        self._group=None

    @classmethod
    def reset_counter(cls):
        cls.id_iterator = count()

    @property
    def group(self):
        return self._group

    @group.setter
    def group(self, val):
        self._group=val

    @property
    def id(self):
        return self._id

    @property
    def max_lessons_per_day(self):
        return self._max_lessons_per_day
    
    @property
    def priority(self):
        return self._priority

    @property
    def uuid(self):
        return self._uuid

    @property
    def teacher(self):
        return self._teacher

    @property
    def room_preference(self):
        return self._room_preference

    @property
    def lessons(self):
        return self._lessons

    def __str__(self):
        return f"id:{self._id} lessons:{self._lessons} priority:{self.priority}"

    def __repr__(self):
        return self.__str__()
