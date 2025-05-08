from itertools import count

from algorithm.entities import Teacher


class Subject:
    id_iterator = count()

    def __init__(self, uuid: str, name: str, hours: int, teacher: Teacher):
        self._uuid = uuid
        self._teacher = teacher
        self._hours = hours
        self._name = name
        self._id = next(self.id_iterator)

    @classmethod
    def reset_counter(cls):
        cls.id_iterator = count()

    @property
    def id(self):
        return self._id

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
    def hours(self):
        return self._hours

    def __str__(self):
        return f"id:{self._id} name:{self._name} teacher:{self._teacher} hours:{self._hours}"

    def __repr__(self):
        return self.__str__()
