import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import HomeIcon from '@mui/icons-material/Home';
import PersonAddAltIcon from '@mui/icons-material/PersonAddAlt';
import PeopleOutlineIcon from '@mui/icons-material/PeopleOutline';
import EventBusyIcon from '@mui/icons-material/EventBusy';
import MessageIcon from '@mui/icons-material/Message';

export const ProfilePath = {text: "Profil", icon: <AccountCircleIcon/>, path: "/profile"}
export const GradebookPath = {text: "Dziennik", icon: <MenuBookIcon/>, path: "/gradebook"}
export const CalendarPath = {text: "Kalendarz", icon: <CalendarMonthIcon/>, path: "/calendar"}
export const HomePath = {text: "Strona główna", icon: <HomeIcon/>, path: "/"}
export const AddUser = {text: "Dodaj użytkownika", icon: <PersonAddAltIcon/>, path: "/admin/add-user"}
export const ListUsers = {text: "Lista użytkowników", icon: <PeopleOutlineIcon/>, path: "/admin/list-users"}
export const TeacherUnavailabilites = {text: "Uzupełnij niedostępność", icon: <EventBusyIcon/>, path: "/teacher/unavailable"}
export const WebsocketTest = {text: "Test WebSocket", icon: <MessageIcon/>, path: "/websocket-test"}