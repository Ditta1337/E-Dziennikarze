import StudentCalendar from "../views/student/StudentCalendar";
import {useStore} from "../store";
import TeacherUnavailabilitiesPreferenceChooser from "../views/teacher/TeacherUnavailabilitiesPreferenceChooser";
import NotFound from "../views/not-found/NotFound";
import {TeacherRole} from "../views/admin/roles";

const UnavailableCalendarRouter = () => {
    const role = useStore((state) => state.user.role[0])
    if(role == TeacherRole){
        return <TeacherUnavailabilitiesPreferenceChooser />
    }
    return <NotFound />
}

export default UnavailableCalendarRouter