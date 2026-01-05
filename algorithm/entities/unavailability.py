class Unavailability:

    def __init__(self, day, lesson):
        self._day = day
        self._lesson = lesson

    @property
    def lesson(self):
        return self._lesson

    @property
    def day(self):
        return self._day

    def __str__(self):
        return f"day:{self._day} lesson:{self._lesson}"

    def __repr__(self):
        return self.__str__()
