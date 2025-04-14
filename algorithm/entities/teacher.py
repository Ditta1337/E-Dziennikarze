from itertools import count

class Teacher:
    id_iterator = count()
    def __init__(self, uuid):
        self._uuid = uuid
        self._id=next(self.id_iterator)

    @classmethod
    def reset_counter(cls):
        cls.id_iterator = count()

    @property
    def uuid(self):
        return self._uuid

    @property
    def id(self):
        return self._id

    def __str__(self):
        return f"id:{self._id}"

    def __repr__(self):
        return self.__str__()
