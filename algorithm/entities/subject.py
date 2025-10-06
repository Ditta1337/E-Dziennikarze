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

    def __init__(self, uuid: str, name: str, hours: int, max_hours_per_day: int, priority: SubjectPriority, teacher: Teacher, room_preference:RoomPreference):
        self._uuid = uuid
        self._teacher = teacher
        self._hours = hours
        self._name = name
        self._max_hours_per_day = max_hours_per_day
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
    def max_hours_per_day(self):
        return self._max_hours_per_day
    
    @property
    def priority(self):
        return self._priority

    @property
    def name(self):
        return self._name

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
    def hours(self):
        return self._hours

    def __str__(self):
        return f"name:{self._name} id:{self._id} hours:{self._hours} priority:{self.priority} teacher_name:{self._teacher.name}"

    def __repr__(self):
        return self.__str__()
