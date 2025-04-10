
class Teacher:
    def __init__(self, teacher_data):
        self._id = teacher_data["teacher_id"]

    @property
    def id(self):
        return self._id

    def __str__(self):
        return f"id:{self._id}"

    def __repr__(self):
        return self.__str__()