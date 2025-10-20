from ortools.sat.python import cp_model
import os
from entities import DataParser
from schemas import ScheduleConfig, SubjectPriority
from entities import Goal, GoalObjective
from entities.data_parser import DataParser
from solution_callback import SolutionCallback

NUM_WORKERS = 8


class Scheduler():
    def __init__(self, schedule_config: ScheduleConfig):
        self.data_parser=DataParser()
        goals, groups,unique_groups_combinations, teachers, subjects, rooms, teaching_days, max_lessons_per_day, latest_starting_lesson, plan_id = self.data_parser.parse_input(schedule_config)
        self.goals = goals
        self.unique_groups_combinations = unique_groups_combinations
        self.groups = groups
        self.teachers = teachers
        self.subjects = subjects
        self.rooms = rooms
        self.teaching_days = teaching_days
        self.max_lessons_per_day = max_lessons_per_day
        self.latest_starting_lesson=latest_starting_lesson
        self.plan_id=plan_id

        self.model = cp_model.CpModel()
        self.solver = cp_model.CpSolver()
        self.vars: dict[tuple[int, int, int, int], cp_model.IntVar] = {}

        self._create_vars()
        self.solution_callback = SolutionCallback(
            self.vars, self.goals, self.groups, self.teachers,
            self.subjects, self.rooms, self.teaching_days, self.max_lessons_per_day,
            self.plan_id,
            self.data_parser
        )

    def _create_vars(self):
        for subject in self.subjects:
            for room in subject.room_preference.allowed:
                for day in range(self.teaching_days):
                    for lesson in range(self.max_lessons_per_day):
                        if any(u.lesson == lesson and u.day == day for u in subject.teacher.unavailability):
                            continue
                        key = (subject.id, room.id, day, lesson)
                        if key not in self.vars:
                            self.vars[key] = self.model.new_bool_var(
                                f"subject:{subject.id} room:{room.id} day:{day} lesson:{lesson}"
                            )

    def build(self):
        self.no_gaps_for_students()
        self.no_more_than_one_lesson_per_teacher()
        self.ensure_subjects_lessons()
        self.lesson_limit_per_day()
        self.no_gaps_between_same_subjects()

    def solve(self):
        self.solver.parameters.num_workers = int(os.getenv("WORKERS", NUM_WORKERS))
        for goal in self.goals:
            print(f"=================goal {goal.function_name}========================")
            self.solve_goal(goal)
        print()
        for goal in self.goals:
            print(goal.function_name,goal.value)
        print()

    def solve_goal(self, goal: Goal):
        self.__getattribute__(goal.function_name)(goal)
        self.solver.parameters.max_time_in_seconds = goal.time
        self.model.__getattribute__(goal.objective.value)(sum(goal.variables))
        status = self.solver.solve(self.model, solution_callback=self.solution_callback)
        self.solution_callback.send_last_solution()

        if status == cp_model.INFEASIBLE:
            print("INFEASIBLE")
        elif status == cp_model.MODEL_INVALID:
            print("MODEL_INVALID")

        goal.value = self.solver.objective_value
        if goal.objective == GoalObjective.MINIMIZE:
            self.model.add(sum(goal.variables) <= int(goal.value))
        else:
            self.model.add(sum(goal.variables) >= int(goal.value))

    def warm_start_from_last_solution(self):
        for var, val in self.solution_callback.last_solution.items():
            self.model.add_hint(var, val)

    # ====================== Constraints ======================
    def no_gaps_for_students(self):
        for combination in self.unique_groups_combinations:
            for day in range(self.teaching_days):
                for lesson in range(self.latest_starting_lesson):
                    prev_subj = [
                        self.vars[subject.id, room.id, day, lesson]
                        for group in combination
                        for subject in group.subjects
                        for room in subject.room_preference.allowed
                        if (subject.id, room.id, day, lesson) in self.vars
                    ]
                    next_subj = [
                        self.vars[subject.id, room.id, day, lesson + 1]
                        for group in combination
                        for subject in group.subjects
                        for room in subject.room_preference.allowed
                        if (subject.id, room.id, day, lesson + 1) in self.vars
                    ]
                    self.model.add(sum(prev_subj)<=1)
                    self.model.add(sum(next_subj)<=1)
                    self.model.add(sum(prev_subj) <= sum(next_subj))
                for lesson in range(self.latest_starting_lesson, self.max_lessons_per_day):
                    prev_subj = [
                        self.vars[subject.id, room.id, day, lesson]
                        for group in combination
                        for subject in group.subjects
                        for room in subject.room_preference.allowed
                        if (subject.id, room.id, day, lesson) in self.vars
                    ]
                    next_subj = [
                        self.vars[subject.id, room.id, day, lesson + 1]
                        for group in combination
                        for subject in group.subjects
                        for room in subject.room_preference.allowed
                        if (subject.id, room.id, day, lesson + 1) in self.vars
                    ]
                    self.model.add(sum(prev_subj)<=1)
                    self.model.add(sum(next_subj)<=1)
                    self.model.add(sum(prev_subj) >= sum(next_subj))


    def no_more_than_one_lesson_per_teacher(self):
        for teacher in self.teachers:
            for day in range(self.teaching_days):
                for lesson in range(self.max_lessons_per_day):
                    possible = [
                        self.vars[subject.id, room.id, day, lesson]
                        for subject in teacher.subjects
                        for room in subject.room_preference.allowed
                        if (subject.id, room.id, day, lesson) in self.vars
                    ]
                    self.model.add(sum(possible) <= 1)

    def ensure_subjects_lessons(self):
        for group in self.groups:
            for subject in group.subjects:
                possible = [
                    self.vars[subject.id, room.id, day, lesson]
                    for room in subject.room_preference.allowed
                    for day in range(self.teaching_days)
                    for lesson in range(self.max_lessons_per_day)
                    if (subject.id, room.id, day, lesson) in self.vars
                ]
                self.model.add(sum(possible) == subject.lessons)

    def lesson_limit_per_day(self):
        for group in self.groups:
            for subject in group.subjects:
                for day in range(self.teaching_days):
                    possible = [
                        self.vars[subject.id, room.id, day, lesson]
                        for room in subject.room_preference.allowed
                        for lesson in range(self.max_lessons_per_day)
                        if (subject.id, room.id, day, lesson) in self.vars
                    ]
                    self.model.add(sum(possible) <= subject.max_lessons_per_day)

    def no_gaps_between_same_subjects(self):
        for group in self.groups:
            for subject in group.subjects:
                for day in range(self.teaching_days):
                    changes = []
                    for lesson in range(self.max_lessons_per_day):
                        subject_changing = self.model.new_bool_var(
                            f"subject_changing_group{group.id}_subject{subject.id}_day{day}_lesson{lesson}"
                        )
                        prev_subj = sum([
                            self.vars[subject.id, room.id, day, lesson]
                            for room in subject.room_preference.allowed
                            if (subject.id, room.id, day, lesson) in self.vars
                        ])
                        if lesson == self.max_lessons_per_day - 1:
                            next_subj = 0
                        else:
                            next_subj = sum([
                                self.vars[subject.id, room.id, day, lesson + 1]
                                for room in subject.room_preference.allowed
                                if (subject.id, room.id, day, lesson + 1) in self.vars
                            ])
                        self.model.add_abs_equality(subject_changing, prev_subj - next_subj)
                        changes.append(subject_changing)
                    self.model.add(sum(changes) <= 2)

    # ====================== Goals ======================
    def goal_balance_day_length(self, goal):
        diff = []
        for group in self.groups:
            for day_i in range(self.teaching_days - 1):
                for day_j in range(day_i + 1, self.teaching_days):
                    schedule_day_i = [
                        self.vars[subject.id, room.id, day_i, lesson]
                        for subject in group.subjects
                        for room in subject.room_preference.allowed
                        for lesson in range(self.max_lessons_per_day)
                        if (subject.id, room.id, day_i, lesson) in self.vars
                    ]
                    schedule_day_j = [
                        self.vars[subject.id, room.id, day_j, lesson]
                        for subject in group.subjects
                        for room in subject.room_preference.allowed
                        for lesson in range(self.max_lessons_per_day)
                        if (subject.id, room.id, day_j, lesson) in self.vars
                    ]
                    partial_diff = self.model.new_int_var(
                        0, self.max_lessons_per_day,
                        f'diff_group{group.id}_day{day_i}_day{day_j}'
                    )
                    self.model.add_abs_equality(partial_diff, sum(schedule_day_i) - sum(schedule_day_j))
                    diff.append(partial_diff)
        goal.variables = diff
        goal.objective = GoalObjective.MINIMIZE

    def goal_subject_types(self, goal):
        penalty = []
        for group in self.groups:
            for subject in group.subjects:
                for day in range(self.teaching_days):
                    for lesson in range(self.max_lessons_per_day):
                        partial_penalty = self.model.new_int_var(
                            0, self.max_lessons_per_day, f'subject{subject.id}_type_penalty_day{day}_lesson{lesson}'
                        )
                        is_assigned = self.model.new_bool_var(
                            f'is_assigned_group{group.id}_subject{subject.id}_day{day}_lesson{lesson}'
                        )
                        self.model.add(is_assigned == sum([
                            self.vars[subject.id, room.id, day, lesson]
                            for room in subject.room_preference.allowed
                            if (subject.id, room.id, day, lesson) in self.vars
                        ]))

                        match subject.priority:
                            case SubjectPriority.EARLY:
                                self.model.add(partial_penalty == lesson * is_assigned)
                            case SubjectPriority.LATE:
                                num_of_lessons_after = self.model.new_int_var(
                                    0, self.max_lessons_per_day,
                                    f'late_subject{subject.id}_lessons_after_day{day}_lesson{lesson}'
                                )
                                self.model.add(num_of_lessons_after == sum([
                                    self.vars[subject.id, room.id, day, lesson_after]
                                    for room in subject.room_preference.allowed
                                    for lesson_after in range(lesson + 1, self.max_lessons_per_day)
                                    if (subject.id, room.id, day, lesson_after) in self.vars
                                ]))
                                self.model.add_multiplication_equality(partial_penalty, [is_assigned, num_of_lessons_after])
                            case SubjectPriority.EDGE:
                                num_of_lessons_after = sum([
                                    self.vars[subject.id, room.id, day, lesson_after]
                                    for room in subject.room_preference.allowed
                                    for lesson_after in range(lesson + 1, self.max_lessons_per_day)
                                    if (subject.id, room.id, day, lesson_after) in self.vars
                                ])
                                late_penalty = self.model.new_int_var(
                                    0, self.max_lessons_per_day,
                                    f'edge_subject{subject.id}_penalty_option_day{day}_lesson{lesson}'
                                )
                                self.model.add(late_penalty == num_of_lessons_after)
                                self.model.add_min_equality(partial_penalty, [lesson * is_assigned, late_penalty])

                        penalty.append(partial_penalty)
        goal.variables = penalty
        goal.objective = GoalObjective.MINIMIZE

    def goal_room_preferences(self, goal):
        preferred = []
        dispreferred = []

        for subject in self.subjects:
            for room in subject.room_preference.preferred:
                preferred.extend([
                    self.vars[subject.id, room.id, day, lesson]
                    for day in range(self.teaching_days)
                    for lesson in range(self.max_lessons_per_day)
                    if (subject.id, room.id, day, lesson) in self.vars
                ])
            for room in subject.room_preference.dispreferred:
                dispreferred.extend([
                    self.vars[subject.id, room.id, day, lesson]
                    for day in range(self.teaching_days)
                    for lesson in range(self.max_lessons_per_day)
                    if (subject.id, room.id, day, lesson) in self.vars
                ])

        goal.variables = [sum(preferred) - sum(dispreferred)]
        goal.objective = GoalObjective.MAXIMIZE

    def goal_subject_time_preferences(self, goal):
        lesson_slots=[lesson*self.vars[subject.id, room.id, day, lesson]
                    for subject in self.subjects
                    for room in self.rooms
                    for day in range(self.teaching_days)
                    for lesson in range(self.latest_starting_lesson)
                    if (subject.id, room.id, day, lesson) in self.vars
                    ]
        goal.variables = lesson_slots
        goal.objective = GoalObjective.MAXIMIZE
