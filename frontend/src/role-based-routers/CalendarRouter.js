import {useStore} from "../store";
import StudentCalendar from "../views/student/StudentCalendar";
import TeacherCalendar from "../views/teacher/TeacherCalendar";
import NotFound from "../views/not-found/NotFound";

const CalendarRouter = () => {
    const role = useStore((state) => state.user.role)
    if(role == "STUDENT"){
        return <StudentCalendar />
    }
    if(role == "TEACHER"){
        return <TeacherCalendar />
    }
    return <NotFound />
}

export default CalendarRouter