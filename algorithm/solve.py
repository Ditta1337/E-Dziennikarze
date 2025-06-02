import json

from ortools.sat.python import cp_model
import numpy as np
from algorithm.entities import Group, Teacher, DataParser, Subject

WORKING_DAYS = 5
# TODO create function that will read number of subjeacts from file.
# class CpSolverSolutionCallback(ortools.sat.python.cp_model_helper.SolutionCallback):
NUM_OF_SUBJECTS = 7


def create_schedule(model, num_of_groups, num_of_teachers, num_of_subjects, max_hours_per_day, working_days):
    shape = (num_of_groups, num_of_teachers, num_of_subjects, max_hours_per_day, working_days)
    schedule = np.empty(shape, dtype=object)
    print(schedule.shape)

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
            if groups_[group_id, hour, day] is not None:
                print(f"Conflict in groups_ at group:{group_id} hour:{hour} day:{day} existing:{groups_[group_id, hour, day]}")
            if teachers_[teacher_id, hour, day] is not None:
                print(f"Conflict in teachers_ at teacher:{teacher_id} hour:{hour} day:{day} existing:{teachers_[teacher_id, hour, day]}")

            if (groups_[group_id, hour, day] is not None) or (teachers_[teacher_id, hour, day] is not None):
                print("Wrong implementation", group_id, teacher_id, subject_id, day, hour)
            else:
                subject_uuid = subjects[subject_id].uuid
                groups_[group_id, hour, day] = subject_uuid
                teachers_[teacher_id, hour, day] = subject_uuid

    return {"groups": groups_3d_to_2d(groups_), "teachers": teachers_3d_to_2d(teachers_)}

def solve(groups: list[Group], teachers: list[Teacher], subjects: list[Subject], max_hours_per_day: int,
          WORKING_DAYS: int):

    model = cp_model.CpModel()
    schedule = create_schedule(model, len(groups), len(teachers), len(subjects), max_hours_per_day, WORKING_DAYS)

    # ensure that each group have enough lessons
    for group_index, group in enumerate(groups):
        for subject in group.subjects:
            teacher_index = teachers.index(subject.teacher)
            subject_index = subjects.index(subject)
            available_slots = schedule[group_index, teacher_index, subject_index, :, :]
            model.add(np.sum(available_slots) == subject.hours)

    # ensure that each group in each hour in each day have no more than one lesson
    for day in range(WORKING_DAYS):
        for hour in range(max_hours_per_day):
            for group_id in range(len(groups)):
                available_teachers = schedule[group_id, :, :, hour, day]
                model.add(np.sum(available_teachers) <= 1)

    # ensure that each teacher in each hour in each day have no more than one lesson
    for day in range(WORKING_DAYS):
        for hour in range(max_hours_per_day):
            for teacher_id in range(len(teachers)):
                available_teachers = schedule[:, teacher_id, :, hour, day]
                model.add(np.sum(available_teachers) <= 1)



    solver = cp_model.CpSolver()
    solver.solve(model)
    return read_schedule_values(schedule, solver, subjects)



if __name__ == "__main__":
    input_groups, input_teachers, input_subjects, input_max_hours_per_day = DataParser.parse_input("input2.json")

    res=solve(input_groups, input_teachers, input_subjects, input_max_hours_per_day,WORKING_DAYS)
    with open("output.json", "w", encoding="utf-8") as f:
        json.dump(res, f, indent=4, ensure_ascii=False)