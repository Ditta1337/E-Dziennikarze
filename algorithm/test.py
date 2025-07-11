def list_all_window_hours(schedule):
    window_hours = []
    i = 0
    n = len(schedule)

    while i < n:
        if schedule[i] == 0:
            start = i
            while i < n and schedule[i] == 0:
                i += 1
            end = i - 1
            if start > 0 and i < n and schedule[start - 1] == 1 and schedule[i] == 1:
                window_hours.extend(range(start, end + 1))
        else:
            i += 1
    return window_hours


def check_no_gaps(schedule, groups, subjects, solver, teaching_days, max_hours_per_day):
    for group in groups:
        for day in range(teaching_days):
            schedule_part = [sum(solver.Value(schedule[group.id, subject.teacher.id, subject.id, day, hour])
                                           for subject in group.subjects)
                                       for hour in range(max_hours_per_day)]
            gaps=list_all_window_hours(schedule_part)
            if gaps:
                for gap in gaps:
                    print(f"group {group} day {day} hour {gap} gap")

def check_subject_hours(schedule, groups, subjects, solver, teaching_days, max_hours_per_day):
    for group in groups:
        for subject in group.subjects:
            check_subject_hours_sum = sum(
                solver.Value(schedule[group.id, subject.teacher.id, subject.id, day, hour])
                for day in range(teaching_days)
                for hour in range(max_hours_per_day)
            )
            if check_subject_hours_sum != subject.hours:
                print(f"group {group.id} subject {subject.name} hours {subject.hours} found {check_subject_hours_sum}")


def check_one_lesson_per_slot(schedule, groups, teachers, subjects, solver, teaching_days, max_hours_per_day):
    for day in range(teaching_days):
        for hour in range(max_hours_per_day):
            for group in groups:
                count = sum(
                    solver.Value(schedule[group.id, teacher.id, subject.id, day, hour])
                    for teacher in teachers
                    for subject in subjects
                )
                if count > 1:
                    print(f"group {group} day {day} hour{hour} {count} subjects in one time")


def check_teacher_conflicts(schedule, groups, subjects, teachers, solver, teaching_days, max_hours_per_day):
    for day in range(teaching_days):
        for hour in range(max_hours_per_day):
            for teacher in teachers:
                count = sum(
                    solver.Value(schedule[group.id, teacher.id, subject.id, day, hour])
                    for group in groups
                    for subject in subjects
                )
                if count > 1:
                    print(f"teacher {teacher} day {day} hour{hour} {count} subjects in one time")


def check_unavailability(schedule, groups, subjects, teachers, solver):
    for teacher in teachers:
        for unavailability in teacher.unavailability:
            count = sum(
                solver.Value(schedule[group.id, teacher.id, subject.id, unavailability.day, unavailability.hour])
                for group in groups
                for subject in subjects
            )
            if count > 0:
                print(
                    f"teacher {teacher} day {unavailability.day} hour {unavailability.hour} is unavailable but have lesion")
