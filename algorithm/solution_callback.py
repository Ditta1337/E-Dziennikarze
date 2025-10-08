from ortools.sat.python.cp_model import CpSolverSolutionCallback
import requests
import os
import numpy as np
from entities import DataParser

class SolutionCallback(CpSolverSolutionCallback):
    def __init__(self, schedule, goals, groups, teachers, subjects,rooms, teaching_days, max_hours_per_day):
        super().__init__()
        self.schedule = schedule
        self.goals= goals
        self.groups = groups
        self.teachers = teachers
        self.subjects = subjects
        self.rooms = rooms
        self.teaching_days = teaching_days
        self.max_hours_per_day = max_hours_per_day
        self.url = os.getenv("CALLBACK_URL")
        self.last_solution= None

    def on_solution_callback(self):
        print(
            "bound:", self.BestObjectiveBound(),
            "objective:", self.ObjectiveValue(),
            "conflicts:", self.NumConflicts(),
            "branches:", self.NumBranches(),
            )
 
        self.last_solution = {var: self.Value(var) for var in self.schedule.values()}
        #print(self.schedule_to_json())

    def schedule_to_json(self):
        groups = {group.id: {"group_id": group.uuid, "schedule": []} for group in self.groups}
        teachers = {teacher.id: {"teacher_id": teacher.uuid, "schedule": []} for teacher in self.teachers}

        for idx, var in self.schedule.items():
            if self.Value(var):
                group, teacher, subject, room, day, hour = idx
                groups[group]["schedule"].append({
                    "hour": hour,
                    "day": day,
                    "subject": DataParser.get_subject_by_id(subject).uuid,
                    "teacher": DataParser.get_teacher_by_id(teacher).uuid,
                    "room": DataParser.get_room_by_id(room).uuid
                })
                teachers[teacher]["schedule"].append({
                    "hour": hour,
                    "day": day,
                    "subject": DataParser.get_subject_by_id(subject).uuid,
                    "group": DataParser.get_group_by_id(group).uuid,
                    "room": DataParser.get_room_by_id(room).uuid
                })

        return {
                "goals": [{'name':goal.function_name, 'value':goal.value } for goal in self.goals],
            "groups": list(groups.values()),
            "teachers": list(teachers.values())
        }
