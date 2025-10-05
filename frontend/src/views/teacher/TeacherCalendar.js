import {useStore} from "../../store";
import {get} from "../../api";
import WeeklyCalendar from "../../components/calendar/WeeklyCalendar";
import {useEffect, useState} from "react";
import TeacherLessonDetails from "../../components/calendar/lesson-details/teacher/TeacherLessonDetails";
import AttendanceModal from "../../components/attendance-modal/AttendanceModal";

const TeacherCalendar = () => {
    const userId = useStore((state) => state.user.userId)
    const [selectedEvent, setSelectedEvent] = useState(null)
    const [checkAttendanceModalOpen, setCheckAttendanceModalOpen] = useState(false)

    const fetchLessons = (startDate, endDate) => {
        return get(`/lesson/all/teacher/${userId}/from/${startDate}/to/${endDate}`)
    }

    useEffect(() => {
        console.log("open attendance modal: " + checkAttendanceModalOpen)
        console.log("selected event: " + selectedEvent)
    }, [selectedEvent]);

    return (
        <>
            <WeeklyCalendar
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