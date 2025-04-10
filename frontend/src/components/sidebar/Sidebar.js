import React, {useState} from 'react';
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
import {HomePath, CalendarPath, GradebookPath, ProfilePath} from "./paths";
import './Sidebar.scss';

const Sidebar = () => {
    const [open, setOpen] = useState(false); // Drawer starts closed
    const role = useStore((state) => state.role);
    const navigation = useNavigate();
    const location = useLocation();

    const handleDrawerToggle = () => {
        setOpen(!open);
    };

    const routes = role === "admin" ? [HomePath, CalendarPath, GradebookPath, ProfilePath]
        : [HomePath, CalendarPath, GradebookPath];

    return (
        <Box className={`sidebar ${open ? 'open' : 'closed'}`}>
            <List>
                <ListItem className="drawer-toggle" onClick={handleDrawerToggle}>
                    <MenuIcon/>
                </ListItem>
                <Divider/>
                {routes.map(({text, icon, path}) => (
                    <ListItemButton
                        key={text}
                        onClick={() => navigation(path)}
                        selected={location.pathname === path}
                    >
                        <ListItemIcon>{icon}</ListItemIcon>
                        {open && <ListItemText className="route-text" primary={text}/>}
                    </ListItemButton>
                ))}
            </List>
        </Box>
    );
};

export default Sidebar;
