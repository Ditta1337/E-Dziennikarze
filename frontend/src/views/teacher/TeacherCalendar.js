import {useStore} from "../../store";
import {get} from "../../api";
import WeeklyCalendar from "../../components/calendar/WeeklyCalendar";
import {useState} from "react";
import TeacherLessonDetails from "../../components/lesson-details/teacher/TeacherLessonDetails";

const TeacherCalendar = () => {
    const userId = useStore((state) => state.user.userId)
    const [selectedEvent, setSelectedEvent] = useState(null)

    const fetchLessons = (startDate, endDate) => {
        return get(`/lesson/all/teacher/${userId}/from/${startDate}/to/${endDate}`)
    }

    return (
        <>
            <WeeklyCalendar
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