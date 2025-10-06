from itertools import count

class Room:
    id_iterator = count()

    def __init__(self, uuid):
        self._id = next(self.id_iterator)
        self._uuid = uuid

    @classmethod
    def reset_counter(cls):
        cls.id_iterator = count()

    @property
    def uuid(self):
        return self._uuid

    @property
    def id(self):
        return self._id

    def __repr__(self):
        return f"id:{self._id} uud:{self._uuid}"
