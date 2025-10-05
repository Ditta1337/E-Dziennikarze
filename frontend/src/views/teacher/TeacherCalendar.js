import {useStore} from "../../store";
import {get} from "../../api";
import WeeklyReadOnlyCalendar from "../../components/calendar/weekly-read-only-calendar/WeeklyReadOnlyCalendar";
import {useState} from "react";
import TeacherLessonDetails from "../../components/calendar/lesson-details/teacher/TeacherLessonDetails";

const TeacherCalendar = () => {
    const userId = useStore((state) => state.user.userId)
    const [selectedEvent, setSelectedEvent] = useState(null)

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
                isOpen={!!selectedEvent}
                onClose={() => setSelectedEvent(null)}
            />
        </>
    )
}

export default TeacherCalendar