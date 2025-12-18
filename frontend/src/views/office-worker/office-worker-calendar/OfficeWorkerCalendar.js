import {Box} from "@mui/material"
import WeeklyReadOnlyCalendar from "../../../components/calendar/weekly-read-only-calendar/WeeklyReadOnlyCalendar"
import GroupTeacherStudentChooser
    from "../../../components/generated-plan/group-teacher-student-chooser/GroupTeacherStudentChooser"
import React, {useEffect, useState} from "react"
import {get} from "../../../api"
import {StudentRole, TeacherRole} from "../../admin/roles"
import "./OfficeWorkerCalendar.scss"
import LessonDetails from "../../../components/calendar/lesson-details/LessonDetails";

const fetchGroupSubjet = async () => {
    return get("group-subject/all")
}

const fetchStudents = async () => {
    return get(`user/all/active/${StudentRole}`)
}

const fetchGroups = async () => {
    return get("group/all")
}

const fetchTeachers = async () => {
    return get(`user/all/active/${TeacherRole}`)
}

const fetchStudentsGroups = async (id) => {
    return get(`/student-group/student/${id}`)
}

const fetchStudentLessons = (userId, startDate, endDate) => {
    return get(`/lesson/all/student/${userId}/from/${startDate}/to/${endDate}`)
}

const fetchTeacherLessons = (userId, startDate, endDate) => {
    return get(`/lesson/all/teacher/${userId}/from/${startDate}/to/${endDate}`)
}

const fetchGroupLessons = (groupId, startDate, endDate) => {
    return get(`/lesson/all/group/${groupId}/from/${startDate}/to/${endDate}`)
}

const OfficeWorkerCalendar = () => {
    const [groupsToDisplay, setGroupsToDisplay] = useState(null)
    const [teacherToDisplay, setTeacherToDisplay] = useState(null)
    const [studentToDisplay, setStudentToDisplay] = useState(null)
    const [fetchingId, setFetchingId] = useState(null)
    const [selectedEvent, setSelectedEvent] = useState(null)
    const [modalOpen, setModalOpen] = useState(false)

    const fetchLessons = async (id, from, to) => {
        try {
            if (!!teacherToDisplay) {
                return await fetchTeacherLessons(id, from, to)
            } else if (!!studentToDisplay) {
                return await fetchStudentLessons(id, from, to)
            } else if (!!groupsToDisplay && groupsToDisplay.length === 1) {
                return fetchGroupLessons(id, from, to)
            } else {
                return []
            }
        } catch (e) {
            console.error(e)
        }
    }

    const handleSelectEvent = (event) => {
        console.log(event)
        setSelectedEvent(event)
        setModalOpen(true)
    }

    useEffect(() => {
        if(!!teacherToDisplay){
            setFetchingId(teacherToDisplay)
        } else if(!!studentToDisplay){
            setFetchingId(studentToDisplay)
        } else if(!!groupsToDisplay && groupsToDisplay.length === 1) {
            setFetchingId(groupsToDisplay[0])
        } else{
            setFetchingId(null)
        }
    }, [groupsToDisplay, teacherToDisplay, studentToDisplay])

    return <Box className="office-worker-calendar">

        <GroupTeacherStudentChooser
            fetchGroupSubject={fetchGroupSubjet}
            fetchStudents={fetchStudents}
            fetchGroups={fetchGroups}
            fetchTeachers={fetchTeachers}
            fetchStudentsGroups={fetchStudentsGroups}
            setGroupsToDisplay={setGroupsToDisplay}
            setTeacherToDisplay={setTeacherToDisplay}
            setStudentToDisplay={setStudentToDisplay}
        />

        <WeeklyReadOnlyCalendar
            fetchingId={fetchingId}
            fetchLessons={fetchLessons}
            onSelectEvent={handleSelectEvent}
        />

        <LessonDetails
            event={selectedEvent}
            isOpen={modalOpen}
            onClose={() => setModalOpen(false)}
        />

    </Box>
}

export default OfficeWorkerCalendar