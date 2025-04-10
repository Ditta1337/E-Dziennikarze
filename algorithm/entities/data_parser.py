import json
from . import Teacher
from . import Group

class DataParser:
    @staticmethod
    def parse_input(filename)->tuple[list["Group"],list["Teacher"],int]:
        data = json.load(open(filename))
        return (DataParser._parse_groups(data["groups"]),
                DataParser._parse_teachers(data["teachers"]),
                data["max_hours_per_day"])

    @staticmethod
    def _parse_groups(groups_list)->list["Group"]:
        return [Group(group_data) for group_data in groups_list]

    @staticmethod
    def _parse_teachers(teachers_list)->list["Teacher"]:
        return [Teacher(teacher_data) for teacher_data in teachers_list]