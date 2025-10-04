import React, {useState} from 'react';
import {Box} from '@mui/material';
import Sidebar from '../sidebar/Sidebar';
import Breadcrumb from "../breadcrumb/Breadcrumb";
import './Card.scss';
import Header from "../header/Header";
import Footer from "../footer/Footer";

const Card = ({children, className = ''}) => {
    const [sidebarOpen, setSidebarOpen] = useState(true);

    return (
        <Box className={`card ${sidebarOpen ? 'sidebar-open' : ''} ${className}`}>
            <Sidebar open={sidebarOpen} toggle={() => setSidebarOpen(prev => !prev)}/>
            <Box className="content">
                <Header/>
                <Breadcrumb/>
                <Box className="main">
                    {children}
                </Box>
                <Footer/>
            </Box>
        </Box>
    );
};

export default Card;