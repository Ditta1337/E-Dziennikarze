from entities import Teacher, Subject, Group
from entities.unavailability import Unavailability
from schemas import ScheduleConfig, TeacherInput, GroupInput, SubjectInput

class DataParser:
    _teachers_by_uuid = {}
    _groups_by_uuid = {}
    _subjects_by_uuid = {}
    _teachers_by_id = {}
    _groups_by_id = {}
    _subjects_by_id = {}

    @classmethod
    def parse_input(cls, schedule_config: ScheduleConfig) -> tuple[list[Group], list[Teacher], list[Subject], int, int]:
        DataParser._parse_all_teachers(schedule_config.teachers)
        DataParser._parse_all_groups(schedule_config.groups)
        return ([group for _, group in cls._groups_by_uuid.items()],
                [teacher for _, teacher in cls._teachers_by_uuid.items()],
                [subject for _, subject in cls._subjects_by_uuid.items()],
                schedule_config.teaching_days,
                schedule_config.max_hours_per_day)

    @staticmethod
    def _parse_all_teachers(teachers: list[TeacherInput]):
        for teacher in teachers:
            DataParser._parse_teacher(teacher)

    @classmethod
    def _parse_teacher(cls, input_teacher:TeacherInput )-> Teacher:
        if not input_teacher.id in cls._teachers_by_uuid:
            teacher = Teacher(input_teacher.id,
                              input_teacher.name,
                              [Unavailability(unavailability.day, unavailability.hour) for unavailability in input_teacher.unavailability] )
            cls._teachers_by_uuid[teacher.uuid] = teacher
            cls._teachers_by_id[teacher.id] = teacher
        return cls._teachers_by_uuid[input_teacher.id]


    @staticmethod
    def _parse_all_subjects(subjects: list[SubjectInput]) -> None:
        for subject in subjects:
            DataParser._parse_subject(subject)

    @staticmethod
    def _parse_subject(input_subject: SubjectInput) -> None:
        if not input_subject.id in DataParser._subjects_by_uuid:
            subject = Subject(input_subject.id, input_subject.name, input_subject.hours, DataParser._teachers_by_uuid[input_subject.teacher_id])
            DataParser._subjects_by_uuid[subject.uuid] = subject
            DataParser._subjects_by_id[subject.id] = subject

    @staticmethod
    def _parse_all_groups(groups_list: list[GroupInput]) -> None:
        for group in groups_list:
            DataParser._parse_group(group)

    @staticmethod 
    def _parse_group(input_group: GroupInput)  -> Group:
        if not input_group.id in DataParser._groups_by_uuid:
            DataParser._parse_all_subjects(input_group.subjects)
            group = Group(input_group.id, input_group.name,[DataParser._subjects_by_uuid[subject.id] for subject in input_group.subjects])
            DataParser._groups_by_uuid[group.uuid] = group
            DataParser._groups_by_id[group.id] = group
        return DataParser._groups_by_uuid[group.uuid]

    @classmethod
    def get_group_by_id(cls, group_id) -> Group:
        return cls._groups_by_id[group_id]

    @classmethod
    def get_teacher_by_id(cls, teacher_id) -> Teacher:
        return cls._teachers_by_id[teacher_id]

    @classmethod
    def get_subject_by_id(cls, subject_id) -> Subject:
        return cls._subjects_by_id[subject_id]

    @classmethod
    def get_group_by_uuid(cls, group_uuid) -> Group:
        return cls._groups_by_uuid[group_uuid]

    @classmethod
    def get_teacher_by_uuid(cls, teacher_uuid) -> Teacher:
        return cls._teachers_by_uuid[teacher_uuid]

    @classmethod
    def get_subject_by_uuid(cls, subject_uuid) -> Subject:
        return cls._subjects_by_uuid[subject_uuid]
