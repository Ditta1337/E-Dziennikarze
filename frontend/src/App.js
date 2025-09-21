import {BrowserRouter, Routes, Route} from 'react-router';
import Home from './views/home/Home';
import Gradebook from "./views/gradebook/Gradebook";
import Calendar from "./views/calendar/Calendar";
import NotFound from "./views/not-found/NotFound";
import Card from "./components/card/Card";
import AddUser from "./views/admin/add-user/AddUser";
import EditUser from "./views/admin/edit-user/EditUser"
import ListUsers from "./views/admin/list-users/ListUsers"
import Profile from "./views/profile/Profile"
import CreateGroup from "./views/create-group/CreateGroup";
import AddStudentsToGroup from "./views/add-students-to-groups/AddStudentsToGroup";
import CreateRoom from "./views/create-room/CreateRoom";
import Attendance from "./views/attendance/Attendance";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route index element={<Card><Home/></Card>}/>
                <Route path="/gradebook" element={<Card><Gradebook/></Card>}/>
                <Route path="/calendar" element={<Card><Calendar/></Card>}/>
                <Route path="/profile" element={<Card><Profile/></Card>}/>

                {/* Admin endpoints */}
                <Route path="/admin/add-user" element={<Card><AddUser/></Card>}/>
                <Route path="/admin/list-users" element={<Card><ListUsers/></Card>}/>
                <Route path="/admin/list-users/edit-user/:id" element={<Card><EditUser/></Card>} />

                {/*TODO make these path avalible for user in gui, right now they are only possible to reach via uri*/}
                <Route path="/create/group" element={<Card><CreateGroup/></Card>} />
                <Route path="/add/students/group" element={<Card><AddStudentsToGroup/></Card>}/>
                <Route path="/create/room" element={<Card><CreateRoom/></Card>}/>
                <Route path="/attendance" element={<Card><Attendance/></Card>}/>

                <Route path="*" element={<NotFound/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
