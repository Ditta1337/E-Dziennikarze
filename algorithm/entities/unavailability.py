class Unavailability:

    def __init__(self, day, hour):
        self._day = day
        self._hour = hour

    @property
    def day(self):
        return self._hour

    @property
    def hour(self):
        return self._day

    def __str__(self):
        return f"day:{self._day} hour:{self._hour}"

    def __repr__(self):
        return self.__str__()
