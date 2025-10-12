import {useStore} from "../store";
import {GuardianRole, StudentRole} from "../views/admin/roles";
import NotFound from "../views/not-found/NotFound";
import GradeListStudent from "../components/grade-list/grade-list-student/GradeListStudent";
import GradeListGuardian from "../components/grade-list/grade-list-guardian/GradeListGuardian";

const GradeRouter = () => {
    const role = useStore((state) => state.user.role)
    if(role === StudentRole){
        return <GradeListStudent />
    }
    if(role === GuardianRole){
        return <GradeListGuardian />
    }
    return <NotFound />
}

export default GradeRouter