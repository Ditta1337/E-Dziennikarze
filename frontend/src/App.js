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
import AddStudentsToGroup from "./views/add-students-to-groups/AddStudentsToGroup";
import CreateRoom from "./views/create-room/CreateRoom";
import CalendarRouter from "./role-based-routers/CalendarRouter";

function App() {

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<LogIn />} />

                <Route element={<ProtectedRoute />}>
                    <Route path="/gradebook" element={<Card><Gradebook /></Card>} />
                    <Route path="/calendar" element={<Card><CalendarRouter /></Card>} />
                    <Route path="/profile" element={<Card><Profile /></Card>} />

                    {/* Admin endpoints */}
                    <Route path="/admin/add-user" element={<Card><AddUser /></Card>} />
                    <Route path="/admin/list-users" element={<Card><ListUsers /></Card>} />
                    <Route path="/admin/list-users/edit-user/:id" element={<Card><EditUser /></Card>} />

                    {/* Other protected endpoints */}
                    <Route path="/create/group" element={<Card><CreateGroup /></Card>} />
                    <Route path="/add/students/group" element={<Card><AddStudentsToGroup /></Card>} />
                    <Route path="/create/room" element={<Card><CreateRoom /></Card>} />
                </Route>

                <Route path="*" element={<NotFound />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;