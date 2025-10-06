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
import {HomePath, CalendarPath, GradebookPath, ProfilePath, AddUser, ListUsers, TeacherUnavailabilites, WebsocketTest} from "./paths";
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
            case AdminRole: return [HomePath, CalendarPath, GradebookPath, ProfilePath, AddUser, ListUsers, TeacherUnavailabilites, WebsocketTest];
            case TeacherRole: return [HomePath, CalendarPath, GradebookPath, ProfilePath, TeacherUnavailabilites, WebsocketTest];
            case StudentRole: return [HomePath, CalendarPath, GradebookPath, ProfilePath, WebsocketTest];
            case GuardianRole: return [HomePath, CalendarPath, GradebookPath, ProfilePath, WebsocketTest];
            case OfficeWorkerRole: return [HomePath, CalendarPath, GradebookPath, ProfilePath, WebsocketTest];
            case PrincipalRole: return [HomePath, CalendarPath, GradebookPath, ProfilePath, WebsocketTest];
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
