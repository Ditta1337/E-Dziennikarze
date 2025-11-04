from ortools.sat.python.cp_model import CpSolverSolutionCallback
import requests
import os
import time
from dotenv import load_dotenv
from entities import DataParser
load_dotenv()


class SolutionCallback(CpSolverSolutionCallback):
    SOLUTION_SEND_INTERVAL = 10

    def __init__(self, schedule, goals, groups, teachers, subjects, rooms,unique_groups_combinations, 
                 teaching_days, max_lessons_per_day,latest_starting_lesson,  plan_id, plan_name, data_parser):
        super().__init__()
        self.schedule = schedule
        self.goals = goals
        self.groups = groups
        self.teachers = teachers
        self.subjects = subjects
        self.rooms = rooms
        self.teaching_days = teaching_days
        self.max_lessons_per_day = max_lessons_per_day
        self.data_parser = data_parser
        self.plan_id = plan_id
        self.plan_name = plan_name
        self.latest_starting_lesson=latest_starting_lesson
        self.unique_groups_combinations = unique_groups_combinations

        self.url = os.getenv("CALLBACK_URL")
        self.api_key = os.getenv("GRADEBOOK_API_KEY")

        self.last_solution = None
        self.last_calculated_solution = None
        self.solution_index = 1
        self.last_send_time = 0.0
        self.required_lessons = sum(s.lessons for s in self.subjects)
        print("required_lessons: ", self.required_lessons )

    def on_solution_callback(self):
        current_time = time.time()
        self.last_solution = {idx: self.Value(var) for idx, var in self.schedule.items()}
        self.last_calculated_solution = {idx: self.Value(var) for idx, var in self.schedule.items()}

        if current_time - self.last_send_time >= self.SOLUTION_SEND_INTERVAL:
            if self.schedule_is_complete():
                self.last_send_time = current_time
                self.send_last_solution()

    def schedule_is_complete(self):
        assigned_lessons = sum(value for value in self.last_solution.values())
        return assigned_lessons == self.required_lessons

    def send_last_solution(self):
        if not self.last_solution:
            return

        headers = {
            "Content-Type": "application/json",
            "X-API-KEY": self.api_key
        }

        try:
            response = requests.post(self.url, json=self.schedule_to_json(), headers=headers, timeout=500000)
            #print(self.schedule_to_json())
            #print(sum(value for value in self.last_solution.values()))
            response.raise_for_status()
            self.solution_index += 1
            self.last_solution = None
        except requests.exceptions.RequestException as e:
            print("request error ", e)

    def schedule_to_json(self):
        groups = {group.id: {"group_id": group.uuid, "schedule": []} for group in self.groups}
        teachers = {teacher.id: {"teacher_id": teacher.uuid, "schedule": []} for teacher in self.teachers}

        for (subject_id, room_id, day, lesson), assigned in self.last_solution.items():
            if assigned:
                subject = self.data_parser.get_subject_by_id(subject_id)
                group = subject.group
                teacher = subject.teacher
                room = self.data_parser.get_room_by_id(room_id)

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
            "name": f"{self.solution_index} - {self.plan_name}",
            "plan_id": self.plan_id,
            "goals": self.parse_goals(),
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
    
    def parse_goals(self):
        goal_values=[]
        for goal in self.goals:
            self.__getattribute__(goal.function_name)
            goal_values.append({"name":goal.function_name, "value":self.__getattribute__(goal.function_name)})
        return goal_values

    
    def goal_room_preferences(self):
        goal_value={"dispreferred":0,"preferred":0,"no_preference":0}

        for (subject_id, room_id, _, _), assigned in self.last_solution.items():
            if assigned:
                subject = self.data_parser.get_subject_by_id(subject_id)
                room = self.data_parser.get_room_by_id(room_id)
                
                if room in subject.room_preference.dispreferred:
                    goal_value["dispreferred"]+=1

                elif room in subject.room_preference.preferred:
                    goal_value["preferred"]+=1

                else:
                    goal_value["no_preference"]+=1

        return goal_value

    def goal_balance_day_length(self):
        goal_value=[0 for _ in range(self.max_lessons_per_day//2)]
        for cimbination in self.unique_groups_combinations:
            schedule=[sum(self.value(self.last_calculated_solution[subject.id, room.id, day, lesson])
                                     for group in cimbination
                                     for subject in group.subjects
                                     for room in subject.room_preference.allowed
                                     for lesson in self.max_lessons_per_day
                                     )
                        for day in self.teaching_days]

            goal_value[(max(schedule)- min (schedule))//2]
        return goal_value

    def goal_subject_time_preferences(self):
        goal_value = {}

        for combination in self.unique_groups_combinations:
            for group in combination:
                for subject in group.subjects:
                    total_penalty = 0

                    for day in range(self.teaching_days):
                        for lesson in range(self.max_lessons_per_day):
                            assigned = any(
                                self.last_solution.get((subject.id, room.id, day, lesson), 0)
                                for room in subject.room_preference.allowed
                            )
                            if not assigned:
                                continue

                            if subject.priority.name == "EARLY":
                                total_penalty += lesson

                            elif subject.priority.name == "LATE":
                                lessons_after = 0
                                for lesson_after in range(lesson + 1, self.max_lessons_per_day):
                                    if any(
                                        self.last_solution.get((subject.id, room.id, day, lesson_after), 0)
                                        for room in subject.room_preference.allowed
                                    ):
                                        lessons_after += 1
                                total_penalty += lessons_after

                            elif subject.priority.name == "EDGE":
                                early_penalty = lesson
                                lessons_after = 0
                                for lesson_after in range(lesson + 1, self.max_lessons_per_day):
                                    if any(
                                        self.last_solution.get((subject.id, room.id, day, lesson_after), 0)
                                        for room in subject.room_preference.allowed
                                    ):
                                        lessons_after += 1
                                late_penalty = lessons_after
                                total_penalty += min(early_penalty, late_penalty)

                    goal_value[total_penalty] = goal_value.get(total_penalty, 0) + 1

        max_penalty = max(goal_value.keys(), default=0)
        penalty_array = [goal_value.get(i, 0) for i in range(max_penalty + 1)]

        return penalty_array

    def goal_early_start(self):
        goal_value=[0 for _ in range(self.latest_starting_lesson)]
        for cimbination in self.unique_groups_combinations:
            schedule=[sum(self.value(self.last_calculated_solution[subject.id, room.id, day, lesson])
                                     for group in cimbination
                                     for subject in group.subjects
                                     for room in subject.room_preference.allowed
                                     for lesson in self.max_lessons_per_day
                                     )
                        for day in self.teaching_days]
            for num_lessons in schedule:
                parial_penalty = self.latest_starting_lesson - num_lessons
                goal_value[parial_penalty]+=1
        return goal_value
