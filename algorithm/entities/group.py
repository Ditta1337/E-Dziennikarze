from . import Subject

class Group:
    def __init__(self, group_data):
        self._id = group_data["group_id"]
        self._subjects = [Subject(subject_data) for subject_data in group_data["subjects"]]

    @property
    def id(self):
        return self._id

    @property
    def subjects(self)->list["Subject"]:
        return self._subjects

    def __repr__(self):
        return f"id:{self._id} subjects:{self._subjects}"