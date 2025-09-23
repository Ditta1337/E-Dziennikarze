from ortools.sat.python.cp_model import CpSolverSolutionCallback
import json
import requests
class SolutionCallback (CpSolverSolutionCallback):

    def __init__(self,url,schedule, groups, subjects, solver, teaching_days, max_hours_per_day):
        CpSolverSolutionCallback.__init__(self)
        self.url=url
        self.schedule=schedule
        self.groups=groups
        self.subjects=subjects
        self.solver=solver
        self.teaching_days=teaching_days
        self.max_hours_per_day=max_hours_per_day

    def on_solution_callback(self):
        print(self.best_objective_bound,self.objective_value,self.	num_conflicts,self.num_branches)
        
        with open("algorithm/data/output.json", "w", encoding="utf-8") as f:
            json.dump(self.schedule_to_json(), f, ensure_ascii=False, indent=4)
        # response = requests.post(self.url, data=self.schedule_to_json())
        # print(response)


    def schedule_to_json(self):
        subject_names = {s.id: s.name for s in self.subjects}
        data = {"groups": [], "teachers": []}

        for group in self.groups:
            group_schedule = []
            for day in range(self.teaching_days):
                day_schedule = []
                for hour in range(self.max_hours_per_day):
                    subject_found = None
                    for subject in group.subjects:
                        val = self.schedule[group.id, subject.teacher.id, subject.id, day, hour]
                        if val is not None and self.Value(val) == 1:
                            subject_found = subject_names[subject.id]
                            break
                    day_schedule.append(subject_found)
                group_schedule.append(day_schedule)
            data["groups"].append({"group_id": group.id, "schedule": group_schedule})

        teacher_ids = list(set(s.teacher.id for s in self.subjects))
        for teacher_id in teacher_ids:
            teacher_schedule = []
            for day in range(self.teaching_days):
                day_schedule = []
                for hour in range(self.max_hours_per_day):
                    subject_found = None
                    for group in self.groups:
                        for subject in group.subjects:
                            if subject.teacher.id != teacher_id:
                                continue
                            val = self.schedule[group.id, teacher_id, subject.id, day, hour]
                            if val is not None and self.Value(val) == 1:
                                subject_found = group.name
                                break
                        if subject_found:
                            break
                    day_schedule.append(subject_found)
                teacher_schedule.append(day_schedule)
            data["teachers"].append({"teacher_id": teacher_id, "schedule": teacher_schedule})
        return data 