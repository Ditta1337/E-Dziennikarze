import WeeklyCalendar from "../../components/calendar/WeeklyCalendar"
import {get} from "../../api"
import {useStore} from "../../store"
import StudentLessonDetails from "../../components/lesson-details/student/StudentLessonDetails"
import {useState} from "react"

const StudentCalendar = () => {
    const userId = useStore((state) => state.user.userId)
    const [selectedEvent, setSelectedEvent] = useState(null)

    const fetchLessons = (startDate, endDate) =>
        get(`/lesson/all/student/${userId}/from/${startDate}/to/${endDate}`)

    return (
        <>
            <WeeklyCalendar
                fetchLessons={fetchLessons}
                onSelectEvent={setSelectedEvent}
            />

            <StudentLessonDetails
                event={selectedEvent}
                isOpen={!!selectedEvent}
                onClose={() => setSelectedEvent(null)}
            />
        </>
    )
};

export default StudentCalendar;
