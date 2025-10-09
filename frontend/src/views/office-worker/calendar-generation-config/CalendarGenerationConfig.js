import GoalFunctionList from "../../../components/calendar-generation-config/goal-function-list/GoalFunctionList";
import {Box} from "@mui/material";
import "./CalendarGenerationConfig.scss"
import {useEffect, useState} from "react";
import { get } from "../../../api"
import GroupSubjectForm from "../../../components/calendar-generation-config/group-subject-form/GroupSubjectForm";

//TODO refactor to a fetch when endpoint ready
const goal_functions = [
    {
        "function_name":"goal_room_preferences",
        "name":"Preferowanie Pokoi",
        "description":"Priorytezuje dopasowanie preferowanych pokoi."
    },
    {
        "function_name":"goal_subject_types",
        "name":"Preferowanie typów lekcji",
        "description":"Nie wiem co to jest XD szywoj niech wytłumaczy."
    },
    {
        "function_name":"goal_ballance_day_lenght",
        "name":"Preferewanie balansu dobowego",
        "description":"Priorytezuje dopasowanie lekcji aby wszystkie klasy zaczynały i kończyły w podobnym czasie"
    }
]

const fetchGroupSubjectData = async () => {
    return get("group-subject/all")
}

const fetchRooms = async () => {
    return get("room/all")
}

const CalendarGenerationConfig = () => {
    const [configData, setConfigData] = useState(null)

    const constructConfigData = (goalPriorities, goalDurations) => {
        const body = {
            goals: goalPriorities.map(goal => ({
                name: goal.function_name,
                time: goalDurations[goal.function_name]
            }))
        }
        setConfigData(body)
    }

    return <>
        <Box className="groups-subjects">
            <GroupSubjectForm fetchGroupSubjectData={fetchGroupSubjectData} fetchRooms={fetchRooms}/>
        </Box>
        <Box className="goal-functions">
            <GoalFunctionList goalFunctions={goal_functions} constructConfigData={constructConfigData}/>
        </Box>
    </>
}

export default CalendarGenerationConfig