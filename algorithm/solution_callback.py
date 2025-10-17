from ortools.sat.python.cp_model import CpSolverSolutionCallback
import requests
import os
import numpy as np
from entities import DataParser
from dotenv import load_dotenv

class SolutionCallback(CpSolverSolutionCallback):
    def __init__(self, schedule, goals, groups, teachers, subjects,rooms, teaching_days, max_lessons_per_day, plan_id):
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
        self.print_schedule()
        self.last_solution = {var: self.Value(var) for var in self.schedule.values()}
        #print(self.schedule_to_json())
        #requests.post(self.url, json=self.schedule_to_json())


    def schedule_to_json(self):
        groups = {group.id: {"group_id": group.uuid, "schedule": []} for group in self.groups}
        teachers = {teacher.id: {"teacher_id": teacher.uuid, "schedule": []} for teacher in self.teachers}

        for idx, var in self.schedule.items():
            if self.Value(var):
                subject, room, day, lesson = idx
                subject=DataParser.get_subject_by_id(subject)
                group=subject.group
                teacher=subject.teacher
                room=DataParser.get_room_by_id(room)
                groups[group.id]["schedule"].append({
                    "lesson": lesson,
                    "day": day,
                    "subject": subject.uuid,
                    "teacher": teacher.uuid,
                    "room": room.uuid
                })
                teachers[teacher.id]["schedule"].append({
                    "lesson": lesson,
                    "day": day,
                    "subject": subject.uuid, 
                    "group": group.uuid,
                    "room": room.uuid
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
                    subject = DataParser.get_subject_by_id(subject_id).uuid
                    room = DataParser.get_room_by_id(room_id).uuid
                    teacher = DataParser.get_teacher_by_id(
                        next(s.teacher.id for s in self.subjects if s.id == subject_id)
                    ).uuid
                    if group.id in [g.id for g in self.groups if subject_id in [s.id for s in g.subjects]]:
                        #timetable[day][lesson] = f"{subject} ({teacher}, {room})"
                        timetable[day][lesson] = f"X"
            for day_idx, lessons in enumerate(timetable):
                print(f"Dzień {day_idx + 1}: {lessons}")
