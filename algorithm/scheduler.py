from asyncio import set_event_loop_policy
from logging import disable
from re import subn
from fastapi import status
from ortools.sat.python import cp_model
import os
import numpy as np
from pandas._libs.hashtable import mode
from entities import goal
from schemas import ScheduleConfig, SubjectPriority
from entities import Goal, GoalObjective
from entities.data_parser import DataParser
from solution_callback import SolutionCallback

class Scheduler():
    def __init__(self, schedule_config: ScheduleConfig):
        goals, groups, teachers, subjects, rooms, teaching_days, max_hours_per_day = DataParser.parse_input(schedule_config)
        self.goals=goals
        self.groups = groups
        self.teachers = teachers
        self.subjects = subjects
        self.rooms = rooms
        self.teaching_days = teaching_days
        self.max_hours_per_day = max_hours_per_day

        self.model = cp_model.CpModel()
        self.solver= cp_model.CpSolver()
        self.vars: dict[tuple[int, int, int, int, int, int], cp_model.IntVar] = {}
        self._create_vars()
        self.solution_callback=SolutionCallback(self.vars, self.goals, self.groups, self.teachers, self.subjects, self.rooms, self.teaching_days, self.max_hours_per_day)

    def _create_vars(self):
        for group in self.groups:
            for subject in group.subjects:
                for room in subject.room_preference.allowed:
                    for day in range(self.teaching_days):
                        for hour in range(self.max_hours_per_day):
                            self.vars[group.id, subject.teacher.id, subject.id, room.id, day, hour]=\
                                    self.model.new_bool_var(f"group:{group.id} teacher:{subject.teacher.id} subject:{subject.id} room:{room.id} day:{day} hour:{hour}")

    def build(self):
       self.no_gaps()
       self.no_more_than_one_lesson_per_teacher()
       self.no_more_than_one_lesson_per_group()
       self.ensure_subjects_hours()
       self.lesson_limit_per_day()
       self.no_gaps_between_same_subjects()


    def solve(self):
        self.solver.parameters.num_workers =int(os.getenv("WORKERS", "12")) 
        for goal in self.goals:
            print(f"=================goal {goal.function_name}========================")
            self.solve_goal(goal)
        for goal in self.goals:
            print(goal.value)

    def solve_goal(self, goal: Goal):
        self.__getattribute__(goal.function_name)(goal)
        self.solver.parameters.max_time_in_seconds=goal.time
        self.model.__getattribute__(goal.objective.value)(sum(goal.variables))
        status=self.solver.solve(self.model, solution_callback=self.solution_callback)

        if status == cp_model.INFEASIBLE:
            print("INFEASIBLE")
        elif status == cp_model.MODEL_INVALID:
            print("MODEL_INVALID")
        #self.warm_start_from_last_solution()
        goal.value = self.solver.objective_value


    def warm_start_from_last_solution(self):
        for var, val in self.solution_callback.last_solution.items():
            self.model.add_hint(var, val)

    def no_gaps(self):
        for day in range(self.teaching_days):
            for group in self.groups:
                for hour in range(self.max_hours_per_day - 1):

                    prev_subj = [
                            self.vars[group.id, subject.teacher.id, subject.id, room.id, day, hour]
                            for subject in group.subjects
                            for room in subject.room_preference.allowed
                            ]
                    next_subj = [
                            self.vars[group.id, subject.teacher.id, subject.id, room.id, day, hour + 1]
                            for subject in group.subjects
                            for room in subject.room_preference.allowed
                            ]
                    self.model.add(sum(prev_subj) >= sum(next_subj))


    def no_more_than_one_lesson_per_teacher(self):
        for teacher in self.teachers:
            for day in range(self.teaching_days):
                for hour in range(self.max_hours_per_day):
                    possible=[
                            self.vars[subject.group.id, teacher.id, subject.id, room.id, day, hour ]
                            for subject in teacher.subjects
                            for room in subject.room_preference.allowed
                            ] 
                    self.model.add(sum(possible)<=1)



    def no_more_than_one_lesson_per_group(self):
        for group in self.groups:
            for day in range(self.teaching_days):
                for hour in range(self.max_hours_per_day):
                    possible=[
                            self.vars[group.id, subject.teacher.id, subject.id, room.id, day, hour ]
                            for subject in group.subjects
                            for room in subject.room_preference.allowed
                            ] 
                    self.model.add(sum(possible)<=1)


    def ensure_subjects_hours(self):
        for group in self.groups:
            for subject in group.subjects:
                possible = [
                        self.vars[group.id, subject.teacher.id, subject.id, room.id, day, hour]
                        for room in subject.room_preference.allowed
                        for day in range(self.teaching_days)
                        for hour in range(self.max_hours_per_day)
                        ] 
                self.model.add(sum(possible) == subject.hours)


    def lesson_limit_per_day(self):
        for group in self.groups:
            for subject in group.subjects:
                for day in range(self.teaching_days):
                    possible = [
                            self.vars[group.id, subject.teacher.id, subject.id, room.id, day, hour]
                            for room in subject.room_preference.allowed
                            for hour in range(self.max_hours_per_day)
                            ]
                    self.model.add(sum(possible)<=subject.max_hours_per_day)
    
    def no_gaps_between_same_subjects(self):
        for group in self.groups:
            for subject in group.subjects:
                for day in range(self.teaching_days):
                    changes=[]
                    for hour in range(self.teaching_days ):
                        subject_changing= self.model.new_bool_var(f"subject_changing_group{group.id}_subject{subject.id}_day{day}_hour{hour}")
                        prev_subj=sum([self.vars[group.id, subject.teacher.id, subject.id, room.id, day, hour]
                                  for room in subject.room_preference.allowed])
                        if hour == self.max_hours_per_day-1:
                            next_subj=0
                        else:
                            next_subj=sum([self.vars[group.id, subject.teacher.id, subject.id, room.id, day, hour+1]
                                  for room in subject.room_preference.allowed])
                        self.model.add_abs_equality(subject_changing, prev_subj-next_subj)
                        changes.append(subject_changing)
                    self.model.add(sum(changes)<=2)


    def goal_ballance_day_lenght(self, goal):
        diff=[]
        for group in self.groups:
            for day_i in range(self.teaching_days-1):
                for day_j in range(day_i+1, self.teaching_days):
                    schedule_day_i = [
                                    self.vars[group.id, subject.teacher.id, subject.id, room.id, day_i, hour ]
                                    for subject in group.subjects
                                    for room in subject.room_preference.allowed
                                    for hour in range(self.max_hours_per_day)
                                    ]
                    schedule_day_j = [
                                    self.vars[group.id, subject.teacher.id, subject.id, room.id, day_j, hour ]
                                    for subject in group.subjects
                                    for room in subject.room_preference.allowed
                                    for hour in range(self.max_hours_per_day)
                                    ]
                    partial_diff=self.model.new_int_var(0, self.max_hours_per_day,'diff_group{group.id}_day{day_i}_day{day_j}')
                    self.model.add_abs_equality(partial_diff, sum(schedule_day_i)-sum(schedule_day_j))
                    diff.append(partial_diff)
        goal.variables=diff
        goal.objective=GoalObjective.MINIMIZE

    def goal_subject_types(self, goal):
        penalty=[]
        for group in self.groups:
            for subject in group.subjects:
                for day in range(self.teaching_days):
                    for hour in range(self.max_hours_per_day):
                         partial_penalty=self.model.new_int_var(0, self.max_hours_per_day, f'subject{subject.id}_type_penalty_day{day}_hour{hour}')
                         is_assigned = self.model.new_bool_var(f'is_assigned_group{group.id}_subject{subject.id}_day{day}_hour{hour}')
                         self.model.add(is_assigned == sum([self.vars[group.id, subject.teacher.id, subject.id, room.id, day, hour]
                                                            for room in subject.room_preference.allowed]))

                         match (subject.priority):

                            case SubjectPriority.EARLY:
                                self.model.add(partial_penalty==hour*is_assigned)

                            case SubjectPriority.LATE:
                                num_of_lessons_after = self.model.new_int_var(0, self.max_hours_per_day,f'late_subject{subject.id}_lessons_after_day{day}_hour{hour}')
                                self.model.add(num_of_lessons_after == sum([self.vars[group.id, subject.teacher.id, subject.id, room.id, day, hour_after]
                                                                            for room in subject.room_preference.allowed
                                                                            for hour_after in range(hour + 1, self.max_hours_per_day)]))
                                self.model.add_multiplication_equality(partial_penalty, [is_assigned, num_of_lessons_after])

                            case SubjectPriority.EDGE:
                                num_of_lessons_after=sum([self.vars[group.id, subject.teacher.id, subject.id, room.id, day, hour_after]
                                                                        for room in subject.room_preference.allowed
                                                                        for hour_after in range(hour+1, self.max_hours_per_day)
                                                                        ])
                                late_penalty=self.model.new_int_var(0, self.max_hours_per_day, f'edge_subject{subject.id}_penalty_option_day{day}_hour{hour}')
                                self.model.add(late_penalty==num_of_lessons_after)


                                self.model.add_min_equality(partial_penalty, [hour*is_assigned,late_penalty])
                         penalty.append(partial_penalty)
        goal.variables=penalty
        goal.objective=GoalObjective.MINIMIZE

    def goal_room_preferences(self, goal):
        preferred=[]
        for subject in self.subjects:
            for room in subject.room_preference.preferred:
                preferred.extend([self.vars[subject.group.id, subject.id, subject.teacher.id, room.id,day, hour]
                                  for day in range(self.teaching_days)
                                  for hour in range(self.max_hours_per_day)])
        dispreferred=[]
        for subject in self.subjects:
            for room in subject.room_preference.dispreferred:
                dispreferred.extend([self.vars[subject.group.id, subject.id, subject.teacher.id, room.id,day, hour]
                                  for day in range(self.teaching_days)
                                  for hour in range(self.max_hours_per_day)])
        reward=[]
        reward.extend(preferred)
        reward.extend(-var for var in dispreferred)
        goal.variables=reward
        goal.objective=GoalObjective.MAXIMIZE
