

class Subject:
    def __init__(self, subject_data):
        self._id = subject_data["subject_id"]
        self._teacher = subject_data["teacher"]
        self._hours = subject_data["hours"]

    @property
    def id(self):
        return self._id

    @property
    def teacher(self):
        return self._teacher

    @property
    def hours(self):
        return self._hours

    def __str__(self):
        return f"id:{self._id} teacher:{self._teacher} hours:{self._hours}"

    def __repr__(self):
        return self.__str__()

