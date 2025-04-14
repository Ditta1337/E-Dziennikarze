import json

from ortools.sat.python import cp_model
import numpy as np
from algorithm.entities import Group,Teacher, DataParser
WORKING_DAYS=5
#TODO create function that will read number of subjeacts from file
# class CpSolverSolutionCallback(ortools.sat.python.cp_model_helper.SolutionCallback):
NUM_OF_SUBJECTS=7


def create_schedule(model, num_of_groups, num_of_teachers,num_of_subjects, max_hours_per_day, working_days):
    shape = ( num_of_groups,num_of_teachers,num_of_subjects, max_hours_per_day, working_days)
    schedule = np.empty(shape, dtype=object)

    for idx in np.ndindex(shape):
        group_id, teacher_id, subject, day, hour = idx
        schedule[idx] = model.NewBoolVar(
            f"class:{group_id} teacher:{teacher_id} subject:{subject} day:{day} hour:{hour}"
        )

    return schedule

def groups_3d_to_2d(array3d):
    res={}
    for idx,array2d in enumerate(array3d):
        res[DataParser.get_group_by_id(idx).uuid]=array2d.tolist()
    return res

def teachers_3d_to_2d(array3d):
    res={}
    for idx,array2d in enumerate(array3d):
        res[DataParser.get_teacher_by_id(idx).uuid]=array2d.tolist()
    return res


def read_schedule_values(schedule, solver):

    groups=np.full((schedule.shape[0],*schedule.shape[3:]),None)
    teachers=np.full((schedule.shape[1],*schedule.shape[3:]),None)
    for idx in np.ndindex(schedule.shape):
        group_id, teacher_id , subject, day, hour = idx
        # print(idx)
        value = solver.Value(schedule[idx])

        if value:
            # print(value)
            if  (not groups[group_id,day,hour] is None) or(not  teachers[teacher_id,day,hour]is None) :
                print("Wrong implementation")
                break
            else:
                # print("\t",DataParser.get_subject_by_id(subject).uuid)
                groups[group_id, day, hour]=DataParser.get_subject_by_id(subject).uuid
                teachers[teacher_id, day, hour]=DataParser.get_subject_by_id(subject).uuid

    return {"classes":groups_3d_to_2d(groups),  "teachers":teachers_3d_to_2d(teachers)}

def solve(groups:list[Group],teachers:list[Teacher],num_of_subjects:int, max_hours_per_day:int, WORKING_DAYS:int):
    model = cp_model.CpModel()

    schedule=create_schedule(model,len(groups),len(teachers),num_of_subjects,max_hours_per_day,WORKING_DAYS)
    #ensure that each class have enough lessons
    for group in groups:
        for subject in group.subjects:
            available_slots=schedule[group.id,subject.teacher.id,subject.id,:,:]
            model.add(np.sum(available_slots)==subject.hours)

    #ensure that each class in each hour in each day have no more than one lesson
    for day in range(WORKING_DAYS):
        for hour in range(max_hours_per_day):
            for group_id in range(len(groups)):
                available_teachers=schedule[group_id,:,:,hour,day]
                model.add(np.sum(available_teachers)<=1)

    #ensure that each teacher in each hour in each day have no more than one lesson
    for day in range(WORKING_DAYS):
        for hour in range(max_hours_per_day):
            for teacher_id in range(len(teachers)):
                available_teachers=schedule[:,teacher_id,:,hour,day]
                model.add(np.sum(available_teachers)<=1)

    solver = cp_model.CpSolver()
    solver.solve(model)
    return read_schedule_values(schedule,solver)


if __name__ == "__main__":
    groups, teachers, max_hours_per_day = DataParser.parse_input("input.json")
    print(groups)
    print(teachers)
    result=solve(groups, teachers,NUM_OF_SUBJECTS, max_hours_per_day, WORKING_DAYS)
    print(json.dumps(result))
    with open("output.json", "w") as f:
        json.dump(result, f, indent=2)