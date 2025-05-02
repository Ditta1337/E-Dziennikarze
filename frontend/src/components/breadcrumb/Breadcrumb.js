import React from 'react';
import {useLocation, useNavigate} from "react-router";
import {Box, Breadcrumbs, Link} from "@mui/material";
import NavigateNextIcon from "@mui/icons-material/NavigateNext";
import "./Breadcrumb.scss"

const Breadcrumb = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const parsedLocation = location.pathname.split("/").filter(Boolean);

    const handleNavigation = (index) => {
        const path = `/${parsedLocation.slice(0, index + 1).join("/")}`;
        navigate(path);
    };

    return (
        <Box className="breadcrumb">
            <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />}>
                {parsedLocation.map((bc, index) => (
                    <Link
                        key={bc}
                        className="link"
                        onClick={() => handleNavigation(index)}
                    >
                        {bc}
                    </Link>
                ))}
            </Breadcrumbs>
        </Box>
    );
};

export default Breadcrumb;