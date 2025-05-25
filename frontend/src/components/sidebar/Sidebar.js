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
import {HomePath, CalendarPath, GradebookPath, ProfilePath, AddUser, ListUsers} from "./paths";
import './Sidebar.scss';

const Sidebar = ({open, toggle}) => {
    const role = useStore((state) => state.role);
    const navigation = useNavigate();
    const location = useLocation();

    const routes = role === "admin" ? [HomePath, CalendarPath, GradebookPath, ProfilePath, AddUser, ListUsers]
        : [HomePath, CalendarPath, GradebookPath];

    return (
        <Box className={`sidebar ${open ? 'open' : ''}`}>
            <List>
                <ListItem className="drawer-toggle" onClick={toggle}>
                    <MenuIcon/>
                </ListItem>
                <Divider/>
                {routes.map(({text, icon, path}) => (
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
