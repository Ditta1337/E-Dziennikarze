import React from 'react';
import {useLocation} from "react-router";
import {Box, Breadcrumbs, Link} from "@mui/material";
import NavigateNextIcon from "@mui/icons-material/NavigateNext";
import "./Breadcrumb.scss"

const Breadcrumb = () => {
    const location = useLocation();

    const parsedLocation = location.pathname.split("/").filter(Boolean);


    return (
        <Box className="breadcrumb">
            <Breadcrumbs separator={<NavigateNextIcon fontSize="small"/>}>
                {parsedLocation.map(bc => (
                    <Link
                        key={bc}
                        className="link"
                    >
                        {bc}
                    </Link>
                ))}
            </Breadcrumbs>
        </Box>
    );
};

export default Breadcrumb;