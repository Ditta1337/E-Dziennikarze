import json
from ortools.sat.python import cp_model
import numpy as np
from algorithm.entities import Group, Teacher, DataParser, Subject, SubjectPriority
from algorithm.test import *

def create_schedule(model, num_of_groups, num_of_teachers, num_of_subjects, teaching_days, max_hours_per_day):
    shape = (num_of_groups, num_of_teachers, num_of_subjects, teaching_days, max_hours_per_day)
    schedule = np.empty(shape, dtype=object)

    for idx in np.ndindex(shape):
        group_id, teacher_id, subject, day, hour = idx
        schedule[idx] = model.NewBoolVar(
            f"group:{group_id} teacher:{teacher_id} subject:{subject} day:{day} hour:{hour}"
        )

    return schedule

def export_schedule_to_json(schedule: np.ndarray, groups, subjects, solver, teaching_days, max_hours_per_day,
                            output_file="output.json"):
    subject_names = {s.id: s.name for s in subjects}
    data = {"groups": [], "teachers": []}

    for group in groups:
        group_schedule = []
        for day in range(teaching_days):
            day_schedule = []
            for hour in range(max_hours_per_day):
                subject_found = None
                for subject in group.subjects:
                    val = schedule[group.id, subject.teacher.id, subject.id, day, hour]
                    if val is not None and solver.Value(val) == 1:
                        subject_found = subject_names[subject.id]
                        break
                day_schedule.append(subject_found)
            group_schedule.append(day_schedule)
        data["groups"].append({"group_id": group.id, "schedule": group_schedule})

    teacher_ids = list(set(s.teacher.id for s in subjects))
    for teacher_id in teacher_ids:
        teacher_schedule = []
        for day in range(teaching_days):
            day_schedule = []
            for hour in range(max_hours_per_day):
                subject_found = None
                for group in groups:
                    for subject in group.subjects:
                        if subject.teacher.id != teacher_id:
                            continue
                        val = schedule[group.id, teacher_id, subject.id, day, hour]
                        if val is not None and solver.Value(val) == 1:
                            subject_found = group.name
                            break
                    if subject_found:
                        break
                day_schedule.append(subject_found)
            teacher_schedule.append(day_schedule)
        data["teachers"].append({"teacher_id": teacher_id, "schedule": teacher_schedule})

    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=4)

def diff_in_num_of_subjects_per_day(model: cp_model.CpModel, schedule,
                                    groups: list[Group], teachers: list[Teacher], subjects: list[Subject],
                                    teaching_days: int, max_hours_per_day: int):
    mse = []
    for group in groups:
        for day_i in range(teaching_days - 1):
            for day_j in range(day_i + 1, teaching_days):
                num_of_subjects_in_day_i = sum(
                    schedule[group.id, subj.teacher.id, subj.id, day_i, hour]
                    for subj in group.subjects
                    for hour in range(max_hours_per_day)
                )
                num_of_subjects_in_day_j = sum(
                    schedule[group.id, subj.teacher.id, subj.id, day_j, hour]
                    for subj in group.subjects
                    for hour in range(max_hours_per_day)
                )

                penalty_var = model.NewIntVar(0, max_hours_per_day ** 2,
                                             f"penalty_group{group.id}_day{day_i}_day{day_j}")
                diff = model.NewIntVar(-max_hours_per_day, max_hours_per_day,
                                       f"diff_group{group.id}_day{day_i}_day{day_j}")
                model.Add(diff == num_of_subjects_in_day_i - num_of_subjects_in_day_j)
                model.AddMultiplicationEquality(penalty_var, [diff, diff])

                mse.append(penalty_var)
    return mse


def ensure_no_gaps(model: cp_model.CpModel, schedule,
                   groups: list[Group], teachers: list[Teacher], subjects: list[Subject],
                   teaching_days: int, max_hours_per_day: int):
    for day in range(teaching_days):
        for group in groups:
            for hour in range(max_hours_per_day - 1):
                prev_subj = [schedule[group.id, subj.teacher.id, subj.id, day, hour] for subj in group.subjects]
                next_subj = [schedule[group.id, subj.teacher.id, subj.id, day, hour + 1] for subj in group.subjects]
                model.Add(sum(prev_subj) >= sum(next_subj))


def no_more_than_one_lesson_per_group(model: cp_model.CpModel, schedule,
                                      groups: list[Group], teachers: list[Teacher], subjects: list[Subject],
                                      teaching_days: int, max_hours_per_day: int):
    for day in range(teaching_days):
        for hour in range(max_hours_per_day):
            for group in groups: model.Add(sum(
                schedule[group.id, teacher.id, subject.id, day, hour]
                for teacher in teachers
                for subject in subjects
            ) <= 1)


def no_more_than_one_lesson_per_teacher(model: cp_model.CpModel, schedule,
                                        groups: list[Group], teachers: list[Teacher], subjects: list[Subject],
                                        teaching_days: int, max_hours_per_day: int):
    for day in range(teaching_days):
        for hour in range(max_hours_per_day):
            for teacher in teachers:
                model.Add(sum(schedule[group.id, teacher.id, subject.id, day, hour]
                              for group in groups
                              for subject in subjects) <= 1)


def ensure_subjects_hours(model: cp_model.CpModel, schedule,
                          groups: list[Group], teachers: list[Teacher], subjects: list[Subject],
                          teaching_days: int, max_hours_per_day: int):
    for group in groups:
        for subject in group.subjects:
            model.Add(sum([schedule[group.id, subject.teacher.id, subject.id, day, hour]
                           for day in range(teaching_days) for hour in range(max_hours_per_day)]) == subject.hours)


def ensure_teachers_unavailability(model: cp_model.CpModel, schedule,
                                   groups: list[Group], teachers: list[Teacher], subjects: list[Subject],
                                   teaching_days: int, max_hours_per_day: int):
    for teacher in teachers:
        for unavailability in teacher.unavailability:
            model.Add(sum(schedule[group.id, teacher.id, subject.id, unavailability.day, unavailability.hour]
                          for group in groups
                          for subject in subjects) == 0)


def solve(groups: list[Group], teachers: list[Teacher], subjects: list[Subject], teaching_days: int,
          max_hours_per_day: int):
    model = cp_model.CpModel()
    schedule = create_schedule(model, len(groups), len(teachers), len(subjects), teaching_days, max_hours_per_day)
    ensure_subjects_hours(model, schedule, groups, teachers, subjects, teaching_days, max_hours_per_day)
    no_more_than_one_lesson_per_group(model, schedule, groups, teachers, subjects, teaching_days, max_hours_per_day)
    no_more_than_one_lesson_per_teacher(model, schedule, groups, teachers, subjects, teaching_days, max_hours_per_day)
    ensure_teachers_unavailability(model, schedule, groups, teachers, subjects, teaching_days, max_hours_per_day)
    ensure_no_gaps(model, schedule, groups, teachers, subjects, teaching_days, max_hours_per_day)

    diff=diff_in_num_of_subjects_per_day(model, schedule, groups, teachers, subjects, teaching_days, max_hours_per_day)
    # model.Add(sum(diff)<3000)
    model.minimize(sum(diff))
    solver = cp_model.CpSolver()
    solver.parameters.num_workers = 12
    solver.parameters.max_time_in_seconds=120
    print("variables ",len(model.Proto().constraints))
    print(solver.parameters)
    status = solver.Solve(model)

    check_no_gaps(schedule, groups, subjects, solver, teaching_days, max_hours_per_day)
    check_subject_hours(schedule, groups, subjects, solver, teaching_days, max_hours_per_day)
    check_one_lesson_per_slot(schedule, groups, teachers, subjects, solver, teaching_days, max_hours_per_day)
    check_teacher_conflicts(schedule, groups, subjects, teachers, solver, teaching_days, max_hours_per_day)
    check_unavailability(schedule, groups, subjects, teachers, solver)

    export_schedule_to_json(schedule, groups, subjects, solver, teaching_days, max_hours_per_day)

if __name__ == "__main__":
    input_groups, input_teachers, input_subjects, input_teaching_days, input_max_hours_per_day \
        = DataParser.parse_input("input2.json")

    solve(input_groups, input_teachers, input_subjects, input_teaching_days, input_max_hours_per_day)
