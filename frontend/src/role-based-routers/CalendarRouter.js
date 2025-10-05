import {useStore} from "../store";
import StudentCalendar from "../views/student/StudentCalendar";
import TeacherCalendar from "../views/teacher/TeacherCalendar";
import NotFound from "../views/not-found/NotFound";
import {StudentRole, TeacherRole} from "../views/admin/roles";

const CalendarRouter = () => {
    const role = useStore((state) => state.user.role[0])
    if(role === StudentRole){
        return <StudentCalendar />
    }
    if(role === TeacherRole){
        return <TeacherCalendar />
    }
    return <NotFound />
}

export default CalendarRouter