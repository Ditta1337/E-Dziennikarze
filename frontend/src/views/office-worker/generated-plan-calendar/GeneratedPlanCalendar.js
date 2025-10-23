import {useParams} from "react-router";
import {get} from "../../../api"
import "./GeneratedPlanCalendar.scss"
import {Box} from "@mui/material";
import GeneratedPlanBrowsableCalendar
    from "../../../components/generated-plan/generated-plan-browsable-calendar/GeneratedPlanBrowsableCalendar";
import GroupTeacherStudentChooser
    from "../../../components/generated-plan/group-teacher-student-chooser/GroupTeacherStudentChooser";
import {StudentRole, TeacherRole} from "../../admin/roles";
import {useState} from "react";

const fetchGeneratedPlan = async (id) => {
    return get(`plan/calculation/plan/${id}`)
}

const fetchGroupSubjet = async () => {
    return get("group-subject/all")
}

const fetchGroups = async () => {
    return get("group/all")
}

const fetchTeachers = async () => {
    return get(`user/all/active/${TeacherRole}`)
}

const fetchStudents = async () => {
    return get(`user/all/active/${StudentRole}`);
}

const fetchStudentsGroups = async (id) => {
    return get(`/student-group/student/${id}`)
}

const fetchSubjects = async () => {
    return get(`/subject/all`)
}

const fetchRooms = async () => {
    return get(`/room/all`)
}

const GeneratedPlanCalendar = () => {
    const {id} = useParams()
    const [groupsToDisplay, setGroupsToDisplay] = useState([])
    const [teacherToDisplay, setTeacherToDisplay] = useState(null)

    return <Box className="generated-plan-calendar">
        <Box className="chooser">
            <GroupTeacherStudentChooser
                fetchGroupSubject={fetchGroupSubjet}
                fetchStudents={fetchStudents}
                fetchGroups={fetchGroups}
                fetchTeachers={fetchTeachers}
                fetchStudentsGroups={fetchStudentsGroups}
                setGroupsToDisplay={setGroupsToDisplay}
                setTeacherToDisplay={setTeacherToDisplay}
            />
        </Box>
        <Box className="browsable-calendar">
            <GeneratedPlanBrowsableCalendar id={id} fetchGeneratedPlan={fetchGeneratedPlan}
                                            fetchTeachers={fetchTeachers}
                                            fetchGroups={fetchGroups}
                                            fetchSubjets={fetchSubjects}
                                            fetchRooms={fetchRooms}
                                            groupsToDisplay={groupsToDisplay}
                                            teacherToDisplay={teacherToDisplay}
            />
        </Box>
    </Box>
}

export default GeneratedPlanCalendar