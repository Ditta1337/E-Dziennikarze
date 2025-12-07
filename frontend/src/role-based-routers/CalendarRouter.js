import {useStore} from "../store";
import StudentCalendar from "../views/student/StudentCalendar";
import TeacherCalendar from "../views/teacher/TeacherCalendar";
import NotFound from "../views/not-found/NotFound";
import {GuardianRole, StudentRole, TeacherRole} from "../views/admin/roles";
import GuardianCalendar from "../views/guardian/guardian-calendar/GuardianCalendar";

const CalendarRouter = () => {
    const role = useStore((state) => state.user.role)
    if(role === StudentRole){
        return <StudentCalendar />
    }
    if(role === TeacherRole){
        return <TeacherCalendar />
    }
    if(role === GuardianRole){
        return <GuardianCalendar />
    }
    return <NotFound />
}

export default CalendarRouter