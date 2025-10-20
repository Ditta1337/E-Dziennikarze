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

    def parse_input(self, schedule_config: ScheduleConfig) -> tuple[list[Goal], list[Group], list[list[Group]], list[Teacher], list[Subject],list[Room], int, int, int, str]:
        Teacher.reset_counter()
        Subject.reset_counter()
        Group.reset_counter()
        Room.reset_counter()

        self._parse_all_goals(schedule_config.goals)
        self._parse_all_rooms(schedule_config.rooms)
        self._parse_all_teachers(schedule_config.teachers)
        self._parse_all_groups(schedule_config.groups)
        return ( self._goals, 
                [group for _, group in self._groups_by_uuid.items()],
                [[self.get_group_by_uuid(uuid) for uuid in grups] for grups in schedule_config.unique_group_combinations],
                [teacher for _, teacher in self._teachers_by_uuid.items()],
                [subject for _, subject in self._subjects_by_uuid.items()],
                [room for _, room in self._rooms_by_uuid.items()],
                TEACHING_DAYS,
                schedule_config.lessons_per_day,
                schedule_config.latest_starting_lesson,
                schedule_config.plan_id,
                )

    def _parse_all_goals(self, goals):
        for goal in goals:
            self._parse_goal(goal)

    def _parse_goal(self, input_goal):
        goal= Goal(input_goal.name, input_goal.time)
        self._goals.append(goal)

    def _parse_all_rooms(self, input_rooms: list[str]):
        for room in input_rooms:
            self._parse_room(room)

    def _parse_all_teachers(self, teachers: list[TeacherInput]):
        for teacher in teachers:
            self._parse_teacher(teacher)

    def _parse_room(self, input_room:str):
        if not input_room in self._rooms_by_uuid:
            room= Room(input_room)
            self._rooms_by_uuid[room.uuid]= room
            self._rooms_by_id[room.id]= room
        return self._rooms_by_uuid[input_room]
            
    def _parse_teacher(self, input_teacher:TeacherInput )-> Teacher:
        if not input_teacher.teacher_id in self._teachers_by_uuid:
            teacher = Teacher(input_teacher.teacher_id,
                              [Unavailability(unavailability.day, unavailability.lesson) for unavailability in input_teacher.unavailability] )
            self._teachers_by_uuid[teacher.uuid] = teacher
            self._teachers_by_id[teacher.id] = teacher
        return self._teachers_by_uuid[input_teacher.teacher_id]


    def _parse_all_subjects(self, subjects:list[SubjectInput]) -> None:
        for subject in subjects:
            self._parse_subject(subject)

    def _parse_subject(self, input_subject: SubjectInput) -> None:
        if not input_subject.subject_id in self._subjects_by_uuid:
            room_preference=RoomPreference(
                    allowed=[self.get_room_by_uuid(room) for room in input_subject.room.allowed],
                    preferred=[self.get_room_by_uuid(room) for room in input_subject.room.preferred],
                    dispreferred=[self.get_room_by_uuid(room) for room in input_subject.room.dispreferred],
                    )
            teacher=self._teachers_by_uuid[input_subject.teacher_id]
            subject = Subject(input_subject.subject_id, input_subject.lessons_per_week,input_subject.max_lessons_per_day, input_subject.type, teacher, room_preference)
            teacher.subjects.append(subject)
            self._subjects_by_uuid[subject.uuid] = subject
            self._subjects_by_id[subject.id] = subject

    def _parse_all_groups(self, groups_list: list[GroupInput]) -> None:
        for group in groups_list:
            self._parse_group(group)

    def _parse_group(self, input_group: GroupInput)  -> Group:
        if not input_group.group_id in self._groups_by_uuid:
            self._parse_all_subjects(input_group.subjects)
            group = Group(input_group.group_id, [self._subjects_by_uuid[subject.subject_id] for subject in input_group.subjects])
            for subject in group.subjects:
                subject.group=group
            self._groups_by_uuid[group.uuid] = group
            self._groups_by_id[group.id] = group
        return self._groups_by_uuid[input_group.group_id]

    def get_group_by_id(self, group_id) -> Group:
        return self._groups_by_id[group_id]

    def get_teacher_by_id(self, teacher_id) -> Teacher:
        return self._teachers_by_id[teacher_id]

    def get_room_by_id(self, room_id) -> Room:
        return self._rooms_by_id[room_id]

    def get_subject_by_id(self, subject_id) -> Subject:
        return self._subjects_by_id[subject_id]

    def get_group_by_uuid(self, group_uuid) -> Group:
        return self._groups_by_uuid[group_uuid]

    def get_teacher_by_uuid(self, teacher_uuid) -> Teacher:
        return self._teachers_by_uuid[teacher_uuid]

    def get_subject_by_uuid(self, subject_uuid) -> Subject:
        return self._subjects_by_uuid[subject_uuid]

    def get_room_by_uuid(self, room_uuid) -> Room:
        return self._rooms_by_uuid[room_uuid]


