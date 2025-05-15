from . import Subject
from itertools import count


class Group:
    id_iterator = count()

    def __init__(self, uuid, name, subjects: list[Subject]):
        self._id = next(self.id_iterator)
        self._uuid = uuid
        self._name = name
        self._subjects = subjects

    # "group_id": "0a64a408-722c-4dbe-a8ed-c7822c2fc45b",
    # "group_name": "4a",
    # "subjects": [

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
    def name(self):
        return self._name

    @property
    def subjects(self) -> list["Subject"]:
        return self._subjects

    def __repr__(self):
        return f"name:{self._name} id:{self._id} uud:{self._uuid}"
