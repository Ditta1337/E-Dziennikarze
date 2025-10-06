from enum import Enum


class GoalObjective(Enum):
    MINIMIZE="minimize"
    MAXIMIZE="maximize"


class Goal:
    def __init__(self,  function_name:str,  time:int,variables= None, objective=None, value=None):
        self.time=time
        self.function_name= function_name
        self.variables=variables
        self.objective=objective
        self.value= value
