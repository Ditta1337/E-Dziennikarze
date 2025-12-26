import {Box, Button} from "@mui/material"
import GroupTeacherStudentChooser
    from "../../../components/generated-plan/group-teacher-student-chooser/GroupTeacherStudentChooser"
import React, {useEffect, useState} from "react"
import {get, put, post, del} from "../../../api"
import {StudentRole, TeacherRole} from "../../admin/roles"
import "./OfficeWorkerCalendar.scss"
import LessonDetails from "../../../components/calendar/lesson-details/LessonDetails";
import WeeklyModifiableCalendar from "../../../components/calendar/weekly-modifiable-calendar/WeeklyModifiableCalendar";
import options from "../../../components/manual-calendar/group-teacher-chooser/GroupTeacherOptions";
import CreateEditDeleteLessonModal
    from "../../../components/calendar/weekly-modifiable-calendar/create-edit-lesson-modal/CreateEditDeleteLessonModal";
import {AppLocale} from "../../../config/localization";
import {format} from "date-fns";
import getDatesWeekday from "../../../util/calendar/getDatesWeekday";
import "./OfficeWorkerCalendar.scss"

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

const fetchSubjects = async () => {
    return get(`/subject/all`)
}

const fetchRooms = async () => {
    return get(`/room/all`)
}

const putModifiedLessons = async (lessons) => {
    return put(`/lesson`, lessons)
}

const postCreatedLessons = async (lessons) => {
    return post(`/lesson`, lessons)
}

const deleteLessons = async (ids) => {
    return post(`/lesson/delete`, {
        ids: ids
    })
}

const makeDateFromDateAndTimeString = (date, time) => {
    return new Date(`${date}T${time}`);
}

const makeCalendarEventsFromFetchedResponse = (response) => {
    return response.map(lessonData => ({
        title: lessonData.subject,
        start: makeDateFromDateAndTimeString(lessonData.date, lessonData.start_time),
        end: makeDateFromDateAndTimeString(lessonData.date, lessonData.end_time),
        teacherId: lessonData.teacher_id,
        roomId: lessonData.roomId,
        room: lessonData.room,
        assignedLessonId: lessonData.assigned_lesson_id,
        plannedLessonId: lessonData.planned_lesson_id,
        subjectId: lessonData.subject_id,
        subject: lessonData.subject,
        groupId: lessonData.group_id,
        groupCode: lessonData.group_code,
        cancelled: lessonData.cancelled,
        modified: lessonData.modified
    }))
}

const makeSendableLesson = (lesson) => {
    return {
        assigned_lesson_id: lesson.assignedLessonId ? lesson.assignedLessonId : null,
        planned_lesson_id: lesson.plannedLessonId ? lesson.plannedLessonId : null,
        group_id: lesson.groupId,
        group_code: lesson.groupCode,
        date: format(lesson.start, AppLocale.dateFormat),
        cancelled: lesson.cancelled,
        modified: lesson.modified,
        subject_id: lesson.subjectId,
        subject: lesson.subject,
        start_time: format(lesson.start, AppLocale.timeFormat),
        end_time: format(lesson.end, AppLocale.timeFormat),
        week_day: getDatesWeekday(lesson.start),
        room_id: lesson.roomId,
        room: lesson.room,
        teacher_id: lesson.teacherId
    }
}

const postModifications = async (modifiedLessons, createdLessons, deletedLessons) => {
    if (!!modifiedLessons && modifiedLessons.length !== 0) {
        const modifiedLessonsToSend = modifiedLessons.map(lesson => makeSendableLesson(lesson))
        await putModifiedLessons(modifiedLessonsToSend)
    }
    if (!!createdLessons && createdLessons.length !== 0) {
        const createdLessonsToSend = createdLessons.map(lesson => makeSendableLesson(lesson))
        await postCreatedLessons(createdLessonsToSend)
    }
    if (!!deletedLessons && deletedLessons.length !== 0) {
        const deletedIds = deletedLessons.map(lesson => lesson.assignedLessonId)
        console.log(deletedIds)
        const result = await deleteLessons(deletedIds)
        console.log(result)
    }
}

const OfficeWorkerCalendar = () => {
    const [groupData, setGroupData] = useState(null)
    const [teacherData, setTeacherData] = useState(null)
    const [subjectData, setSubjectData] = useState(null)
    const [roomData, setRoomData] = useState(null)

    const [groupsToDisplay, setGroupsToDisplay] = useState(null)
    const [teacherToDisplay, setTeacherToDisplay] = useState(null)
    const [studentToDisplay, setStudentToDisplay] = useState(null)
    const [fetchingId, setFetchingId] = useState(null)
    const [events, setEvents] = useState([])

    const [selectedEvent, setSelectedEvent] = useState(null)
    const [infoModalOpen, setInfoModalOpen] = useState(false)

    const [editMode, setEditMode] = useState(false)
    const [editedEvent, setEditedEvent] = useState(null)
    const [isCreated, setIsCreated] = useState(false)
    const [editModalOpen, setEditModalOpen] = useState(false)
    const [perspective, setPerspective] = useState(null)

    const [createdLessons, setCreatedLessons] = useState([])
    const [modifiedLessons, setModifiedLessons] = useState([])
    const [deletedLessons, setDeletedLessons] = useState([])

    const fetchData = async () => {
        try {
            const groupResult = await fetchGroups()
            const teacherResult = await fetchTeachers()
            const subjectResult = await fetchSubjects()
            const roomResult = await fetchRooms()

            setGroupData(groupResult.data)
            setTeacherData(teacherResult.data)
            setSubjectData(subjectResult.data)
            setRoomData(roomResult.data)
        } catch (e) {
            console.error(e)
        }
    }

    const fetchLessons = async (from, to) => {
        try {
            if (!!teacherToDisplay) {
                const result = await fetchTeacherLessons(fetchingId, from, to)
                setEvents(makeCalendarEventsFromFetchedResponse(result.data))
            } else if (!!studentToDisplay) {
                const result = await fetchStudentLessons(fetchingId, from, to)
                setEvents(makeCalendarEventsFromFetchedResponse(result.data))
            } else if (!!groupsToDisplay && groupsToDisplay.length === 1) {
                const result = await fetchGroupLessons(fetchingId, from, to)
                setEvents(makeCalendarEventsFromFetchedResponse(result.data))
            } else {
                setEvents([])
            }
        } catch (e) {
            console.error(e)
        }
    }

    const handleSelectEvent = (event) => {
        if (editMode) {
            setEditedEvent(event)
            setIsCreated(false)
            setEditModalOpen(true)
        } else {
            setSelectedEvent(event)
            setInfoModalOpen(true)
        }
    }

    const handleSelectSlot = ({start, end}) => {
        setEditedEvent({
            start: start,
            end: end
        })
        setIsCreated(true)
        setEditModalOpen(true)
    }

    const handleEventSave = (event) => {
        if (isCreated) {
            setCreatedLessons([...createdLessons, event])
            setEvents([...events, event])
        } else {
            setModifiedLessons([...modifiedLessons, event])
            const eventsWithoutModified = events.filter(e => e.assignedLessonId !== event.assignedLessonId)
            setEvents([...eventsWithoutModified, event])
        }
    }

    const handleEventDeletion = (event) => {
        setDeletedLessons([...deletedLessons, event])
        const eventsWithoutDeleted = events.filter(e => e.assignedLessonId !== event.assignedLessonId)
        setEvents(eventsWithoutDeleted)
    }

    const handleSave = () => {
        try {
            postModifications(modifiedLessons, createdLessons, deletedLessons)
        } catch (e) {
            console.log(e)
        }
        setCreatedLessons([])
        setModifiedLessons([])
        setDeletedLessons([])
    }

    useEffect(() => {
        if (!!teacherToDisplay) {
            setFetchingId(teacherToDisplay)
            setPerspective(options.TeacherPerspective)
        } else if (!!studentToDisplay) {
            setFetchingId(studentToDisplay)
            setPerspective(options.GroupPerspective)
        } else if (!!groupsToDisplay && groupsToDisplay.length === 1) {
            setFetchingId(groupsToDisplay[0])
            setPerspective(options.GroupPerspective)
        } else {
            setFetchingId(null)
            setPerspective(null)
        }
    }, [groupsToDisplay, teacherToDisplay, studentToDisplay])

    useEffect(() => {
        fetchData()
    }, []);

    return <Box className="office-worker-calendar">

        <Box className="chooser-actions-container">
            <GroupTeacherStudentChooser
                fetchGroupSubject={fetchGroupSubjet}
                fetchStudents={fetchStudents}
                fetchGroups={fetchGroups}
                fetchTeachers={fetchTeachers}
                fetchStudentsGroups={fetchStudentsGroups}
                setGroupsToDisplay={setGroupsToDisplay}
                setTeacherToDisplay={setTeacherToDisplay}
                setStudentToDisplay={setStudentToDisplay}
                disabled={editMode}
            />

            <Button
                variant="contained"
                onClick={() => {
                    if (editMode) {
                        handleSave()
                    }
                    setEditMode(!editMode)
                }}
                disabled={!perspective}
                className="edit-calendar-button"
            >
                {editMode ? "Zapisz" : "Edytuj tydzień"}
            </Button>
        </Box>

        <WeeklyModifiableCalendar
            fetchingId={fetchingId}
            events={events}
            updateEvents={fetchLessons}
            onSelectEvent={handleSelectEvent}
            onSelectSlot={handleSelectSlot}
            editMode={editMode}
        />

        <LessonDetails
            event={selectedEvent}
            isOpen={infoModalOpen}
            onClose={() => setInfoModalOpen(false)}
        />

        <CreateEditDeleteLessonModal
            event={editedEvent}
            perspective={perspective}
            groupData={groupData}
            teacherData={teacherData}
            subjectData={subjectData}
            roomData={roomData}
            isOpen={editModalOpen}
            onClose={() => setEditModalOpen(false)}
            isCreated={isCreated}
            saveEvent={handleEventSave}
            deleteEvent={handleEventDeletion}
        />
    </Box>
}

export default OfficeWorkerCalendar