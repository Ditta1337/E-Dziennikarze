import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import HomeIcon from '@mui/icons-material/Home';
import PersonAddAltIcon from '@mui/icons-material/PersonAddAlt';
import PeopleOutlineIcon from '@mui/icons-material/PeopleOutline';
import EventBusyIcon from '@mui/icons-material/EventBusy';
import MessageIcon from '@mui/icons-material/Message';
import GradeIcon from '@mui/icons-material/Grade';
import CalculateIcon from '@mui/icons-material/Calculate';
import GroupsIcon from '@mui/icons-material/Groups';
import GroupAddIcon from '@mui/icons-material/GroupAdd';
import MeetingRoomIcon from '@mui/icons-material/MeetingRoom';
import EditCalendarIcon from '@mui/icons-material/EditCalendar';

export const ProfilePath = {text: "Profil", icon: <AccountCircleIcon/>, path: "/profile"}
export const GradebookPath = {text: "Dziennik", icon: <MenuBookIcon/>, path: "/gradebook"}
export const CalendarPath = {text: "Kalendarz", icon: <CalendarMonthIcon/>, path: "/calendar"}
export const HomePath = {text: "Strona główna", icon: <HomeIcon/>, path: "/"}
export const AddUser = {text: "Dodaj użytkownika", icon: <PersonAddAltIcon/>, path: "/admin/add-user"}
export const ListUsers = {text: "Lista użytkowników", icon: <PeopleOutlineIcon/>, path: "/admin/list-users"}
export const TeacherUnavailabilites = {text: "Uzupełnij niedostępność", icon: <EventBusyIcon/>, path: "/teacher/unavailable"}
export const Grades = {text: "Oceny", icon: <GradeIcon/>, path: "/grades"}
export const CalendarConfigurationList = {text: "Generowanie planów", icon: <CalculateIcon/>, path: "/calendar/generation/config/list"}
export const ManualCalendarList = {text: "Tworzenie planów", icon: <EditCalendarIcon/>, path: "/calendar/manual/list"}
export const CreateGroup = {text: "Utwórz grupę", icon: <GroupsIcon/>, path: "/create/group"}
export const EditGroups = {text: "Edytuj grupy", icon: <GroupAddIcon/>, path: "/edit/group"}
export const ManageRooms = {text: "Zarządzaj salami", icon: <MeetingRoomIcon/>, path: "/manage/rooms"}
export const Chat = {text: "Chat", icon: <MessageIcon/>, path: "/chat"}