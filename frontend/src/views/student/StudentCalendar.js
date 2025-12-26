import WeeklyCalendar from "../../components/calendar/weekly-read-only-calendar/WeeklyCalendar"
import {get} from "../../api"
import {useStore} from "../../store"
import StudentLessonDetails from "../../components/calendar/lesson-details/student/StudentLessonDetails"
import {useState} from "react"

const fetchLessons = (userId, startDate, endDate) =>
    get(`/lesson/all/student/${userId}/from/${startDate}/to/${endDate}`)

const StudentCalendar = () => {
    const userId = useStore((state) => state.user.userId)
    const [selectedEvent, setSelectedEvent] = useState(null)

    return (
        <>
            <WeeklyCalendar
                fetchingId={userId}
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
