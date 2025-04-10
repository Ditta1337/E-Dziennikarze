import json

from ortools.sat.python import cp_model
import numpy as np
from algorithm.entities import Group,Teacher, DataParser
WORKING_DAYS=5
#TODO create function that will read number of subjeacts from file
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

def prepare_output(array3d):
    res={}
    for idx,array2d in enumerate(array3d):
        res[idx]=array2d.tolist()
    return res


def read_schedule_values(schedule, solver):

    groups=np.full((schedule.shape[0],*schedule.shape[3:]),-1)
    teachers=np.full((schedule.shape[1],*schedule.shape[3:]),-1)
    for idx in np.ndindex(schedule.shape):
        group_id, teacher_id , subject, day, hour = idx
        # print(idx)
        value = solver.Value(schedule[idx])

        if value:
            # print(value)
            if groups[group_id,day,hour]!=-1 or teachers[teacher_id,day,hour]!=-1 :
                print("Wrong implementation")
                break
            else:
                groups[group_id, day, hour]=subject
                teachers[teacher_id, day, hour]=subject

    return {"classes":prepare_output(groups),  "teachers":prepare_output(teachers)}

def solve(groups:list[Group],teachers:list[Teacher],num_of_subjects:int, max_hours_per_day:int, WORKING_DAYS:int):
    model = cp_model.CpModel()

    schedule=create_schedule(model,len(groups),len(teachers),num_of_subjects,max_hours_per_day,WORKING_DAYS)
    #ensure that each class have enough lessons
    for group in groups:
        for subject in group.subjects:
            available_slots=schedule[group.id,subject.teacher,subject.id,:,:]
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