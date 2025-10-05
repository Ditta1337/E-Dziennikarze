import React, {useState} from "react";
import {useStore} from "../../store";
import {get} from "../../api";
import WeeklyReadOnlyCalendar from "../../components/calendar/weekly-read-only-calendar/WeeklyReadOnlyCalendar";
import {useState} from "react";
import TeacherLessonDetails from "../../components/calendar/lesson-details/teacher/TeacherLessonDetails";
import AttendanceModal from "../../components/attendance-modal/AttendanceModal";

const TeacherCalendar = () => {
    const userId = useStore((state) => state.user.userId)
    const [selectedEvent, setSelectedEvent] = useState(null)
    const [checkAttendanceModalOpen, setCheckAttendanceModalOpen] = useState(false)

    const fetchLessons = (startDate, endDate) => {
        return get(`/lesson/all/teacher/${userId}/from/${startDate}/to/${endDate}`)
    }

    return (
        <>
            <WeeklyReadOnlyCalendar
                fetchLessons={fetchLessons}
                onSelectEvent={setSelectedEvent}
            />

            <TeacherLessonDetails
                event={selectedEvent}
                setCheckAttendanceModalOpen={setCheckAttendanceModalOpen}
                isOpen={!!selectedEvent}
                onClose={() => setSelectedEvent(null)}
            />

            <AttendanceModal
                isOpen={checkAttendanceModalOpen}
                onClose={() => {
                    setCheckAttendanceModalOpen(false)
                    setSelectedEvent(null)
                }}
                groupName={selectedEvent?.groupCode}
                groupId={selectedEvent?.groupId}
                subjectId={selectedEvent?.subjectId}
                assignedLessonId={selectedEvent?.assignedLessonId}
            />
        </>
    )
}

export default TeacherCalendar