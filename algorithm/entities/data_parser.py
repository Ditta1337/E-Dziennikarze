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
    def parse_input(cls, filename: str) -> tuple[list["Group"], list["Teacher"],list["Subject"], int]:
        data = json.load(open(filename))
        DataParser._parse_all_groups(data["groups"])
        return ([group for _, group in cls._groups_by_uuid.items()],
                [teacher for _, teacher in cls._teachers_by_uuid.items()],
                [subject for _, subject in cls._subjects_by_uuid.items()],
                data["max_hours_per_day"])

    @staticmethod
    def _parse_all_groups(groups_list) -> None:
        for group_data in groups_list:
            print(group_data)
            DataParser._parse_group_by_uuid(group_data["group_uuid"], group_data["subjects"])

    @classmethod
    def _parse_teacher_by_uuid(cls, teacher_uuid: str) -> Teacher:
        if not teacher_uuid in cls._teachers_by_uuid:
            teacher = Teacher(teacher_uuid)
            cls._teachers_by_uuid[teacher.uuid] = teacher
            cls._teachers_by_id[teacher.id] = teacher
        return cls._teachers_by_uuid[teacher_uuid]

    @classmethod
    def _parse_subject_by_uuid(cls, subject_uuid: str, hours: int, teacher_uuid: str) -> Subject:
        if not subject_uuid in cls._subjects_by_uuid:
            subject = Subject(subject_uuid, hours, DataParser._parse_teacher_by_uuid(teacher_uuid))
            cls._subjects_by_uuid[subject.uuid] = subject
            cls._subjects_by_id[subject.id] = subject
        return cls._subjects_by_uuid[subject_uuid]

    @classmethod
    def _parse_group_by_uuid(cls, group_uuid: str, subjects: list[dict]) -> Group:
        if not group_uuid in cls._groups_by_uuid:
            group = Group(group_uuid,
                          [DataParser._parse_subject_by_uuid(
                              subject["subject_uuid"], subject["hours"], subject["teacher_uuid"])
                              for subject in subjects])
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
