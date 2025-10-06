from itertools import count
from entities import Unavailability

class Teacher:
    id_iterator = count()
    def __init__(self, uuid: str,name: str,unavailability: list[ Unavailability]):
        self._uuid = uuid
        self._id=next(self.id_iterator)
        self._name=name
        self._subjects=[]
        self._unavailability=unavailability


    @classmethod
    def reset_counter(cls):
        cls.id_iterator = count()

    @property
    def uuid(self):
        return self._uuid

    @property
    def subjects(self):
        return self._subjects

    @property
    def unavailability(self):
        return self._unavailability

    @property
    def name(self):
        return self._name

    @property
    def id(self):
        return self._id

    def __str__(self):
        return f"id:{self._id} name:{self.name} uuid:{self._uuid}"

    def __repr__(self):
        return self.__str__()
