from ortools.sat.python.cp_model import CpSolverSolutionCallback
import requests
import os
import numpy as np
from entities import DataParser
from dotenv import load_dotenv

class SolutionCallback(CpSolverSolutionCallback):
    def __init__(self, schedule, goals, groups, teachers, subjects,rooms, teaching_days, max_lessons_per_day, plan_id, data_parser):
        super().__init__()

        load_dotenv()

        self.schedule = schedule
        self.goals= goals
        self.groups = groups
        self.teachers = teachers
        self.subjects = subjects
        self.rooms = rooms
        self.teaching_days = teaching_days
        self.max_lessons_per_day = max_lessons_per_day
        self.data_parser= data_parser
        self.url = os.getenv("CALLBACK_URL")
        self.last_solution= None
        self.plan_id=plan_id
        print(self.url)

    def on_solution_callback(self):
        print(
            "bound:", self.BestObjectiveBound(),
            "objective:", self.ObjectiveValue(),
            "conflicts:", self.NumConflicts(),
            "branches:", self.NumBranches(),
            )
        #self.print_schedule()
        self.last_solution = {var: self.Value(var) for var in self.schedule.values()}
        requests.post(self.url, json=self.schedule_to_json())


    def schedule_to_json(self):
        groups = {group.id: {"group_id": group.uuid, "schedule": []} for group in self.groups}
        teachers = {teacher.id: {"teacher_id": teacher.uuid, "schedule": []} for teacher in self.teachers}

        for idx, var in self.schedule.items():
            if self.Value(var):
                subject, room, day, lesson = idx
                subject=self.data_parser.get_subject_by_id(subject)
                group=subject.group
                teacher=subject.teacher
                room=self.data_parser.get_room_by_id(room)
                groups[group.id]["schedule"].append({
                    "lesson": lesson,
                    "day": day,
                    "subject_id": subject.uuid,
                    "teacher_id": teacher.uuid,
                    "room_id": room.uuid
                })
                teachers[teacher.id]["schedule"].append({
                    "lesson": lesson,
                    "day": day,
                    "subject_id": subject.uuid, 
                    "group_id": group.uuid,
                    "room_id": room.uuid
                })

        return {
            "plan_id": self.plan_id,
            "goals": [{'name':goal.function_name, 'value':goal.value } for goal in self.goals],
            "groups": list(groups.values()),
            "teachers": list(teachers.values())
        }
    def print_schedule(self):
        print("\n=== PLAN LEKCJI DLA GRUP ===")
        for group in self.groups:
            print(f"\nGrupa: {group.uuid}")
            timetable = [["-" for _ in range(self.max_lessons_per_day)] for _ in range(self.teaching_days)]
            for (subject_id, room_id, day, lesson), var in self.schedule.items():
                if self.Value(var):
                    subject = self.data_parser.get_subject_by_id(subject_id).uuid
                    room = self.data_parser.get_room_by_id(room_id).uuid
                    teacher = self.data_parser.get_teacher_by_id(
                        next(s.teacher.id for s in self.subjects if s.id == subject_id)
                    ).uuid
                    if group.id in [g.id for g in self.groups if subject_id in [s.id for s in g.subjects]]:
                        timetable[day][lesson] = f"{subject}"
                        #timetable[day][lesson] = f"X"
            for day_idx, lessons in enumerate(timetable):
                print(f"Dzień {day_idx + 1}: {lessons}")
