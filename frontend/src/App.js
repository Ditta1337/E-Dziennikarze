import { BrowserRouter, Routes, Route } from 'react-router';
import LogIn from "./views/log-in/LogIn";
import NotFound from "./views/not-found/NotFound";
import Card from "./components/card/Card";
import ProtectedRoute from "./components/protected-route/ProtectedRoute";
import Gradebook from "./views/gradebook/Gradebook";
import Profile from "./views/profile/Profile";
import AddUser from "./views/admin/add-user/AddUser";
import ListUsers from "./views/admin/list-users/ListUsers";
import EditUser from "./views/admin/edit-user/EditUser";
import CreateGroup from "./views/create-group/CreateGroup";
import EditGroups from "./views/edit-groups/EditGroups";
import CreateRoom from "./views/manage-rooms/ManageRooms";
import CalendarRouter from "./role-based-routers/CalendarRouter";
import UnavailableCalendarRouter from "./role-based-routers/UnavailableCalendarRouter";
import CalendarGenerationConfig from "./views/office-worker/calendar-generation-config/CalendarGenerationConfig";
import GradeRouter from "./role-based-routers/GradeRouter";
import Chat from "./views/chat/Chat";
import CalendarGenerationConfigList
    from "./views/office-worker/calendar-generation-config-list/CalendarGenerationConfigList";
import { AdminRole, TeacherRole, StudentRole, GuardianRole, OfficeWorkerRole, PrincipalRole } from './views/admin/roles.js';
import GeneratedPlansList from "./views/office-worker/generated-plans-list/GeneratedPlansList";
import GeneratedPlanCalendar from "./views/office-worker/generated-plan-calendar/GeneratedPlanCalendar";
import ManualPlanCalendarEditor from "./views/office-worker/manual-plan-calendar-editor/ManualPlanCalendarEditor";
import ManualPlansList from "./views/office-worker/manual-plans-list/ManualPlansList";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<LogIn />} />

                <Route element={<ProtectedRoute />}>
                    <Route path="/profile" element={<Card><Profile /></Card>} />
                    <Route path="/gradebook" element={<Card><Gradebook /></Card>} />
                    <Route path="/calendar" element={<Card><CalendarRouter /></Card>} />
                    <Route path="/chat" element={<Card><Chat /></Card>} />
                </Route>

                <Route element={<ProtectedRoute allowedRoles={[AdminRole]} />}>
                    <Route path="/admin/add-user" element={<Card><AddUser /></Card>} />
                    <Route path="/admin/list-users" element={<Card><ListUsers /></Card>} />
                    <Route path="/admin/list-users/edit-user/:id" element={<Card><EditUser /></Card>} />
                </Route>

                <Route element={<ProtectedRoute allowedRoles={[AdminRole, TeacherRole]} />}>
                    <Route path="/teacher/unavailable" element={<Card><UnavailableCalendarRouter /></Card>} />
                </Route>

                <Route element={<ProtectedRoute allowedRoles={[StudentRole, GuardianRole]} />}>
                    <Route path="/grades" element={<Card><GradeRouter /></Card>} />
                </Route>

                <Route element={<ProtectedRoute allowedRoles={[AdminRole, OfficeWorkerRole, PrincipalRole]} />}>
                    <Route path="/create/group" element={<Card><CreateGroup /></Card>} />
                    <Route path="/edit/group" element={<Card><EditGroups /></Card>} />
                    <Route path="/manage/rooms" element={<Card><CreateRoom /></Card>} />
                    <Route path="/calendar/generation/config" element={<Card><CalendarGenerationConfig /></Card>} />
                    <Route path="/calendar/generation/config/list" element={<Card><CalendarGenerationConfigList /></Card>} />
                    <Route path="/calendar/generation/config/:id" element={<Card><CalendarGenerationConfig /></Card>} />
                    <Route path="/calendar/generated/list/:planId" element={<Card><GeneratedPlansList /></Card>} />
                    <Route path="/calendar/generated/plan/:id" element={<Card><GeneratedPlanCalendar /></Card>} />
                    <Route path="/calendar/manual/plan/:id" element={<Card><ManualPlanCalendarEditor /></Card>} />
                    <Route path="/calendar/manual/list" element={<Card><ManualPlansList /></Card>} />
                </Route>

                <Route path="*" element={<NotFound />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;