import {useStore} from "../store";
import StudentCalendar from "../views/student/StudentCalendar";
import TeacherCalendar from "../views/teacher/TeacherCalendar";
import NotFound from "../views/not-found/NotFound";
import {AdminRole, GuardianRole, OfficeWorkerRole, PrincipalRole, StudentRole, TeacherRole} from "../views/admin/roles";
import GuardianCalendar from "../views/guardian/guardian-calendar/GuardianCalendar";
import OfficeWorkerCalendar from "../views/office-worker/office-worker-calendar/OfficeWorkerCalendar";

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
    if(role === OfficeWorkerRole || role === AdminRole || role === PrincipalRole){
        return <OfficeWorkerCalendar />
    }
    return <NotFound />
}

export default CalendarRouter