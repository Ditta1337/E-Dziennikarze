import React, {useEffect, useState} from "react";
import {get} from "../../api";
import {Box, CircularProgress, Typography} from "@mui/material";
import CallIcon from '@mui/icons-material/Call';
import "./Footer.scss";

function Header() {
    const [schoolPhone, setSchoolPhone] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchSchoolPhone()
    }, []);


    const fetchSchoolPhone = async () => {
        try {
            const schoolPhoneData = await get("/property/name/schoolPhoneNumber");
            setSchoolPhone(schoolPhoneData.data.value);
            setLoading(false);
        } catch (error) {
            console.error("Failed to fetch school data:", error);
        }
    }


    return (
        <Box className="footer">
            {loading ? <CircularProgress/> :
                <Box className="contact-info">
                    <Typography>Kontakt:</Typography>
                    <Box className="contact-item">
                        <CallIcon fontSize="small"/>
                        <Typography>{schoolPhone}</Typography>
                    </Box>
                </Box>
            }
        </Box>);
}

export default Header;
