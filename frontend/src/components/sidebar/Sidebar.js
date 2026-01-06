import React from 'react';
import {
    List,
    ListItem,
    Box,
    ListItemIcon,
    ListItemButton,
    ListItemText,
    Divider
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import {useLocation, useNavigate} from 'react-router';
import {useStore} from "../../store";
import {
    CalendarPath,
    ProfilePath,
    AddUser,
    ListUsers,
    TeacherUnavailabilites,
    Grades, CalendarConfigurationList,
    CreateGroup,
    ManageRooms,
    EditGroups,
    PropertyEditor,
    ManualCalendarList,
    Chat
} from "./paths";
import './Sidebar.scss';
import {
    AdminRole,
    GuardianRole,
    OfficeWorkerRole,
    PrincipalRole,
    StudentRole,
    TeacherRole
} from "../../views/admin/roles";

const Sidebar = ({open, toggle}) => {
    const role = useStore((state) => state.user.role);
    const navigation = useNavigate();
    const location = useLocation();


    const getRoutesByRole = (role) => {
        switch (role) {
            case AdminRole:
                return [CalendarPath, ProfilePath, AddUser, ListUsers, TeacherUnavailabilites, CreateGroup, EditGroups, ManageRooms, CalendarConfigurationList, ManualCalendarList, Chat, PropertyEditor];
            case TeacherRole:
                return [CalendarPath, ProfilePath, TeacherUnavailabilites, Chat];
            case StudentRole:
                return [CalendarPath, ProfilePath, Grades, Chat];
            case GuardianRole:
                return [CalendarPath, ProfilePath, Grades, Chat];
            case OfficeWorkerRole:
                return [CalendarPath, ProfilePath, CreateGroup, EditGroups, ManageRooms, CalendarConfigurationList, ManualCalendarList, Chat, PropertyEditor];
            case PrincipalRole:
                return [CalendarPath, ProfilePath, CreateGroup, EditGroups, ManageRooms, CalendarConfigurationList, ManualCalendarList, Chat, PropertyEditor];
        }
    }

    return (
        <Box className={`sidebar ${open ? 'open' : ''}`}>
            <List>
                <ListItem className="drawer-toggle" onClick={toggle}>
                    <MenuIcon/>
                </ListItem>
                <Divider/>
                {getRoutesByRole(role).map(({text, icon, path}) => (
                    <ListItemButton
                        className="route-button"
                        key={text}
                        onClick={() => navigation(path)}
                        selected={location.pathname === path}
                    >
                        <ListItemIcon className="route-icon">{icon}</ListItemIcon>
                        {open && <ListItemText className="route-text" primary={text}/>}
                    </ListItemButton>
                ))}
            </List>
        </Box>
    );
};

export default Sidebar;
