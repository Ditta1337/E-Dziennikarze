from entities import Teacher, Subject, Group, Room, Goal, Unavailability, RoomPreference
from schemas import ScheduleConfig, TeacherInput, GroupInput, SubjectInput 
TEACHING_DAYS=5

class DataParser:
    _teachers_by_uuid = {}
    _groups_by_uuid = {}
    _subjects_by_uuid = {}
    _rooms_by_uuid = {}
    _teachers_by_id = {}
    _groups_by_id = {}
    _subjects_by_id = {}
    _rooms_by_id = {}
    _goals= []

    @classmethod
    def parse_input(cls, schedule_config: ScheduleConfig) -> tuple[list[Goal], list[Group], list[list[Group]], list[Teacher], list[Subject],list[Room], int, int]:
        DataParser._parse_all_goals(schedule_config.goals)
        DataParser._parse_all_rooms(schedule_config.rooms)
        DataParser._parse_all_teachers(schedule_config.teachers)
        DataParser._parse_all_groups(schedule_config.groups)
        return ( cls._goals, 
                [group for _, group in cls._groups_by_uuid.items()],
                [[DataParser.get_group_by_uuid(uuid) for uuid in grups] for grups in schedule_config.unique_groups_combinations],
                [teacher for _, teacher in cls._teachers_by_uuid.items()],
                [subject for _, subject in cls._subjects_by_uuid.items()],
                [room for _, room in cls._rooms_by_uuid.items()],
                TEACHING_DAYS,
                schedule_config.lessons_per_day)

    @staticmethod
    def _parse_all_goals(goals):
        for goal in goals:
            DataParser._parse_goal(goal)

    @classmethod
    def _parse_goal(cls, input_goal):
        goal= Goal(input_goal.name, input_goal.time)
        cls._goals.append(goal)

    @staticmethod
    def _parse_all_rooms(input_rooms: list[str]):
        for room in input_rooms:
            DataParser._parse_room(room)

    @staticmethod
    def _parse_all_teachers(teachers: list[TeacherInput]):
        for teacher in teachers:
            DataParser._parse_teacher(teacher)

    @classmethod
    def _parse_room(cls, input_room:str):
        if not input_room in cls._rooms_by_uuid:
            room= Room(input_room)
            cls._rooms_by_uuid[room.uuid]= room
            cls._rooms_by_id[room.id]= room
        return cls._rooms_by_uuid[input_room]
            
    @classmethod
    def _parse_teacher(cls, input_teacher:TeacherInput )-> Teacher:
        if not input_teacher.teacher_id in cls._teachers_by_uuid:
            teacher = Teacher(input_teacher.teacher_id,
                              [Unavailability(unavailability.day, unavailability.lesson) for unavailability in input_teacher.unavailability] )
            cls._teachers_by_uuid[teacher.uuid] = teacher
            cls._teachers_by_id[teacher.id] = teacher
        return cls._teachers_by_uuid[input_teacher.teacher_id]


    @staticmethod
    def _parse_all_subjects(subjects:list[SubjectInput]) -> None:
        for subject in subjects:
            DataParser._parse_subject(subject)

    @staticmethod
    def _parse_subject(input_subject: SubjectInput) -> None:
        if not input_subject.subject_id in DataParser._subjects_by_uuid:
            room_preference=RoomPreference(
                    allowed=[DataParser.get_room_by_uuid(room) for room in input_subject.room.allowed],
                    preferred=[DataParser.get_room_by_uuid(room) for room in input_subject.room.preferred],
                    dispreferred=[DataParser.get_room_by_uuid(room) for room in input_subject.room.dispreferred],
                    )
            teacher=DataParser._teachers_by_uuid[input_subject.teacher_id]
            subject = Subject(input_subject.subject_id, input_subject.lessons_per_week,input_subject.max_lessons_per_day, input_subject.type, teacher, room_preference)
            teacher.subjects.append(subject)
            DataParser._subjects_by_uuid[subject.uuid] = subject
            DataParser._subjects_by_id[subject.id] = subject

    @staticmethod
    def _parse_all_groups(groups_list: list[GroupInput]) -> None:
        for group in groups_list:
            DataParser._parse_group(group)

    @staticmethod 
    def _parse_group(input_group: GroupInput)  -> Group:
        if not input_group.group_id in DataParser._groups_by_uuid:
            DataParser._parse_all_subjects(input_group.subjects)
            group = Group(input_group.group_id, [DataParser._subjects_by_uuid[subject.subject_id] for subject in input_group.subjects])
            for subject in group.subjects:
                subject.group=group
            DataParser._groups_by_uuid[group.uuid] = group
            DataParser._groups_by_id[group.id] = group
        return DataParser._groups_by_uuid[input_group.group_id]

    @classmethod
    def get_group_by_id(cls, group_id) -> Group:
        return cls._groups_by_id[group_id]

    @classmethod
    def get_teacher_by_id(cls, teacher_id) -> Teacher:
        return cls._teachers_by_id[teacher_id]

    @classmethod
    def get_room_by_id(cls, room_id) -> Room:
        return cls._rooms_by_id[room_id]

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
    @classmethod
    def get_room_by_uuid(cls, room_uuid) -> Room:
        return cls._rooms_by_uuid[room_uuid]


