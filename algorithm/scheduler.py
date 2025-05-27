import json

from ortools.sat.python import cp_model
import numpy as np
from algorithm.entities import Group, Teacher, DataParser, Subject

WORKING_DAYS = 5


def create_schedule(model, num_of_groups, num_of_teachers, num_of_subjects, max_hours_per_day, working_days):
    shape = (num_of_groups, num_of_teachers, num_of_subjects, max_hours_per_day, working_days)
    schedule = np.empty(shape, dtype=object)

    for idx in np.ndindex(shape):
        group_id, teacher_id, subject, hour, day = idx
        schedule[idx] = model.NewBoolVar(
            f"group:{group_id} teacher:{teacher_id} subject:{subject} hour:{hour} day:{day}"
        )

    return schedule


def teachers_3d_to_2d(array3d):
    res = []
    for idx, array2d in enumerate(array3d):
        teacher_dict = {}
        teacher_dict["teacher_id"] = DataParser.get_teacher_by_id(idx).uuid
        teacher_dict["schedule"] = array2d.tolist()
        res.append(teacher_dict)
    return res


def groups_3d_to_2d(array3d):
    res = []
    for idx, array2d in enumerate(array3d):
        group_dict = {}
        group_dict["group_id"] = DataParser.get_group_by_id(idx).uuid
        group_dict["schedule"] = array2d.tolist()
        res.append(group_dict)
    return res

def read_schedule_values(schedule, solver, subjects):
    groups_ = np.full((schedule.shape[0], *schedule.shape[3:]), None)
    teachers_ = np.full((schedule.shape[1], *schedule.shape[3:]), None)
    for idx in np.ndindex(schedule.shape):
        group_id, teacher_id, subject_id, hour, day = idx
        value = solver.Value(schedule[idx])
        if value:
            if (groups_[group_id, hour, day] is not None) or (teachers_[teacher_id, hour, day] is not None):
                print("Something went wrong")
            else:
                subject_uuid = subjects[subject_id].uuid
                groups_[group_id, hour, day] = subject_uuid
                teachers_[teacher_id, hour, day] = subject_uuid

    return {"groups": groups_3d_to_2d(groups_), "teachers": teachers_3d_to_2d(teachers_)}

def solve_yielding(groups: list[Group], teachers: list[Teacher], subjects: list[Subject], max_hours_per_day: int,
                   WORKING_DAYS: int):
    model = cp_model.CpModel()
    schedule = create_schedule(model, len(groups), len(teachers), len(subjects), max_hours_per_day, WORKING_DAYS)

    for group_index, group in enumerate(groups):
        for subject in group.subjects:
            teacher_index = teachers.index(subject.teacher)
            subject_index = subjects.index(subject)
            available_slots = schedule[group_index, teacher_index, subject_index, :, :]
            model.add(np.sum(available_slots) == subject.hours)

    for day in range(WORKING_DAYS):
        for hour in range(max_hours_per_day):
            for group_id in range(len(groups)):
                model.add(np.sum(schedule[group_id, :, :, hour, day]) <= 1)

            for teacher_id in range(len(teachers)):
                model.add(np.sum(schedule[:, teacher_id, :, hour, day]) <= 1)

    solver = cp_model.CpSolver()

    status = solver.Solve(model)

    if status == cp_model.FEASIBLE or status == cp_model.OPTIMAL:
        result = read_schedule_values(schedule, solver, subjects)
        yield result
    else:
        yield {"error": "No solution found"}




if __name__ == "__main__":
    input_groups, input_teachers, input_subjects, input_max_hours_per_day = DataParser.parse_input("input2.json")
    generator = solve_yielding(input_groups, input_teachers, input_subjects,
                               input_max_hours_per_day, WORKING_DAYS)

    solution_count = 0
    last_solution = None

    for partial_result in generator:
        solution_count += 1
        last_solution = partial_result
    print(solution_count)
    with open("output.json", "w", encoding="utf-8") as f:
        json.dump(last_solution, f, indent=4, ensure_ascii=False)