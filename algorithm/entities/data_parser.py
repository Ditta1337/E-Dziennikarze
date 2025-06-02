import json

from . import Teacher, Subject, Group


class DataParser:
    _teachers_by_uuid = {}
    _groups_by_uuid = {}
    _subjects_by_uuid = {}
    _teachers_by_id = {}
    _groups_by_id = {}
    _subjects_by_id = {}

    @classmethod
    def parse_input(cls, filename: str) -> tuple[list["Group"], list["Teacher"], list["Subject"], int,int]:
        data = json.load(open(filename))
        DataParser._parse_all_teachers(data["teachers"])
        DataParser._parse_all_groups(data["groups"])
        return ([group for _, group in cls._groups_by_uuid.items()],
                [teacher for _, teacher in cls._teachers_by_uuid.items()],
                [subject for _, subject in cls._subjects_by_uuid.items()],
                data["teaching_days"],
                data["max_hours_per_day"])

    @staticmethod
    def _parse_all_teachers(teachers_list):
        for teacher in teachers_list:
            unavailability = [(unavailability["day"], unavailability["hour"]) for unavailability in
                              teacher["unavailability"]]
            DataParser._parse_teacher(teacher["teacher_id"],
                                      teacher["teacher_name"],
                                      teacher["hours"],
                                      unavailability)

    @classmethod
    def _parse_teacher(cls, teacher_uuid: str, name: str, hours: int, unavailability: list[tuple[int,int]]) -> Teacher:
        if not teacher_uuid in cls._teachers_by_uuid:
            teacher = Teacher(teacher_uuid, name, hours, unavailability)
            cls._teachers_by_uuid[teacher.uuid] = teacher
            cls._teachers_by_id[teacher.id] = teacher
        return cls._teachers_by_uuid[teacher_uuid]

    @staticmethod
    def _parse_all_groups(groups_list) -> None:
        for group_data in groups_list:
            subjects = []
            for subject_data in group_data["subjects"]:
                subject = DataParser._parse_subject(subject_data["subject_id"],
                                                    subject_data["subject_name"],
                                                    subject_data["hours"],
                                                    subject_data["teacher_id"])
                subjects.append(subject)
            DataParser._parse_group(group_data["group_id"], group_data["group_name"], subjects)

    @classmethod
    def _parse_subject(cls, subject_uuid: str, subject_name, hours: int, teacher_uuid: str) -> Subject:
        if not subject_uuid in cls._subjects_by_uuid:
            subject = Subject(subject_uuid, subject_name, hours, DataParser.get_teacher_by_uuid(teacher_uuid))
            cls._subjects_by_uuid[subject.uuid] = subject
            cls._subjects_by_id[subject.id] = subject
        return cls._subjects_by_uuid[subject_uuid]

    @classmethod
    def _parse_group(cls, group_uuid: str, name: str, subjects: list["Subject"]) -> Group:
        if not group_uuid in cls._groups_by_uuid:
            group = Group(group_uuid, name, subjects)
            cls._groups_by_uuid[group.uuid] = group
            cls._groups_by_id[group.id] = group
        return cls._groups_by_uuid[group_uuid]

    @classmethod
    def get_group_by_id(cls, group_id):
        return cls._groups_by_id[group_id]

    @classmethod
    def get_teacher_by_id(cls, teacher_id):
        return cls._teachers_by_id[teacher_id]

    @classmethod
    def get_subject_by_id(cls, subject_id):
        return cls._subjects_by_id[subject_id]

    @classmethod
    def get_group_by_uuid(cls, group_uuid):
        return cls._groups_by_uuid[group_uuid]

    @classmethod
    def get_teacher_by_uuid(cls, teacher_uuid):
        return cls._teachers_by_uuid[teacher_uuid]

    @classmethod
    def get_subject_by_uuid(cls, subject_uuid):
        return cls._subjects_by_uuid[subject_uuid]
