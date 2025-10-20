import {useStore} from "../store";
import TeacherUnavailabilitiesPreferenceChooser from "../views/teacher/TeacherUnavailabilitiesPreferenceChooser";
import NotFound from "../views/not-found/NotFound";
import {TeacherRole} from "../views/admin/roles";

const UnavailableCalendarRouter = () => {
    const role = useStore((state) => state.user.role)
    if(role === TeacherRole){
        return <TeacherUnavailabilitiesPreferenceChooser />
    }
    return <NotFound />
}

export default UnavailableCalendarRouter