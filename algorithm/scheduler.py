import json
from ortools.sat.python import cp_model
import numpy as np
from algorithm.entities import Group, Teacher, DataParser, Subject


def create_schedule(model, num_of_groups, num_of_teachers, num_of_subjects, working_days, max_hours_per_day):
    shape = (num_of_groups, num_of_teachers, num_of_subjects, working_days, max_hours_per_day)
    schedule = np.empty(shape, dtype=object)

    for idx in np.ndindex(shape):
        group_id, teacher_id, subject, day, hour = idx
        schedule[idx] = model.NewBoolVar(
            f"group:{group_id} teacher:{teacher_id} subject:{subject} day:{day} hour:{hour}"
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
        group_id, teacher_id, subject_id, day, hour = idx
        value = solver.Value(schedule[idx])
        if value:
            if (groups_[group_id, day, hour] is not None) or (teachers_[teacher_id, day, hour] is not None):
                print("Something went wrong")
            else:
                subject_uuid = subjects[subject_id].uuid
                groups_[group_id, day, hour] = subject_uuid
                teachers_[teacher_id, day, hour] = subject_uuid

    return {"groups": groups_3d_to_2d(groups_), "teachers": teachers_3d_to_2d(teachers_)}


def solve(groups: list[Group], teachers: list[Teacher], subjects: list[Subject], WORKING_DAYS: int,
          max_hours_per_day: int):
    model = cp_model.CpModel()
    schedule = create_schedule(model, len(groups), len(teachers), len(subjects), WORKING_DAYS, max_hours_per_day)

    # ensure exact hours per subject per group
    for group_index, group in enumerate(groups):
        for subject in group.subjects:
            teacher_index = teachers.index(subject.teacher)
            subject_index = subjects.index(subject)
            available_slots = schedule[group_index, teacher_index, subject_index, :, :]
            model.add(np.sum(available_slots) == subject.hours)

    for hour in range(max_hours_per_day):
        for day in range(WORKING_DAYS):
            # ensure no more than one lesson per group at one moment
            for group_id in range(len(groups)):
                model.add(np.sum(schedule[group_id, :, :, day, hour]) <= 1)

            # ensure no more than one lesson per teacher at one moment
            for teacher_id in range(len(teachers)):
                model.add(np.sum(schedule[:, teacher_id, :, day, hour]) <= 1)

    # ensure teachers unavailability
    for teacher in teachers:
        for unavailability in teacher.unavailability:
            model.add(np.sum(schedule[:, teacher.id, :, unavailability.day, unavailability.hour]) == 0)

    solver = cp_model.CpSolver()
    status = solver.Solve(model)


    result = read_schedule_values(schedule, solver, subjects)
    return result


if __name__ == "__main__":
    input_groups, input_teachers, input_subjects, teaching_days, input_max_hours_per_day\
        = DataParser.parse_input("input.json")
    solution = solve(input_groups, input_teachers, input_subjects, teaching_days, input_max_hours_per_day)

    print(solution)
    with open("output.json", "w", encoding="utf-8") as f:
        json.dump(solution, f, indent=4, ensure_ascii=False)
