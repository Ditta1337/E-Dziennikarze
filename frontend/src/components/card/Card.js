import React, {useState} from 'react';
import {Box, Typography} from '@mui/material';
import Sidebar from '../sidebar/Sidebar';
import './Card.scss';

const Card = ({children, className = ''}) => {
    const [sidebarOpen, setSidebarOpen] = useState(true);

    return (
        <Box className={`card ${sidebarOpen ? 'sidebar-open' : ''} ${className}`}>
            <Sidebar open={sidebarOpen} toggle={() => setSidebarOpen(prev => !prev)}/>
            <Box className="content">
                <Box className="header">
                    <Typography>Here will be header content like school name, user icon and logout button</Typography>
                </Box>

                <Box className="main">
                    {children}
                </Box>

                <Box className="footer">
                    <Typography>Here will be footer content like contact links etc.</Typography>
                </Box>
            </Box>
        </Box>
    );
};

export default Card;