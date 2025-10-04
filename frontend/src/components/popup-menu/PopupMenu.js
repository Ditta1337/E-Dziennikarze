import React, { useState, useRef } from 'react';
import { Box, List, ListItem, ListItemText } from '@mui/material';
import './PopupMenu.scss';

function PopupMenu({ children, options }) {
    const [isOpen, setIsOpen] = useState(false);
    const timeoutRef = useRef(null);

    const handleMouseEnter = () => {
        clearTimeout(timeoutRef.current);
        setIsOpen(true);
    };

    const handleMouseLeave = () => {
        timeoutRef.current = setTimeout(() => {
            setIsOpen(false);
        }, 200);
    };

    return (
        <Box
            className="popup-menu-container"
            onMouseEnter={handleMouseEnter}
            onMouseLeave={handleMouseLeave}
        >
            {children}
            {isOpen && (
                <List component="nav" className="popup-menu">
                    {options.map((option, index) => (
                        <ListItem button key={index} onClick={option.onClick} className="popup-menu-item">
                            <ListItemText primary={option.label} />
                        </ListItem>
                    ))}
                </List>
            )}
        </Box>
    );
}

export default PopupMenu;

