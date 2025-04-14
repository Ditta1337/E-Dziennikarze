from . import Subject
from itertools import count


class Group:
    id_iterator = count()
    def __init__(self, uuid,subjects:list[Subject]):
        self._id = next(self.id_iterator)
        self._uuid = uuid
        self._subjects = subjects

    @classmethod
    def reset_counter(cls):
        cls.id_iterator = count()

    @property
    def uuid(self):
        return self._uuid

    @property
    def id(self):
        return self._id

    @property
    def subjects(self) -> list["Subject"]:
        return self._subjects

    def __repr__(self):
        return f"id:{self._id} subjects:{self._subjects}"
