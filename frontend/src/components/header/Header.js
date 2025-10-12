import React, {useEffect, useState} from "react";
import {get} from "../../api";
import {useStore} from "../../store";
import {Avatar, Box, CircularProgress, Typography} from "@mui/material";
import {useNavigate} from "react-router";
import "./Header.scss";
import PopupMenu from "../popup-menu/PopupMenu";
import NotificationBell from "../notification-bell/NotificationBell";

function Header() {
    const userId = useStore((state) => state.user.userId);
    const logout = useStore((state) => state.logout);
    const navigate = useNavigate();

    const [schoolName, setSchoolName] = useState("Szkoła");
    const [schoolLogo, setSchoolLogo] = useState("");
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchSchoolData()
            .then(fetchUserData);
    }, [userId]);


    const fetchSchoolData = async () => {
        try {
            const [schoolNameData, schoolImageData] = await Promise.all([
                get("/property/name/schoolFullName"),
                get("/property/name/schoolLogoBase64")]
            );
            setSchoolName(schoolNameData.data.value);
            setSchoolLogo(schoolImageData.data.value);
        } catch (error) {
            console.error("Failed to fetch school data:", error);
        }
    }

    const fetchUserData = async () => {
        if (!userId) {
            setLoading(false);
            return;
        }
        try {
            const userData = await get(`/user/${userId}`);
            setUser(userData.data);
            setLoading(false);
        } catch (error) {
            console.error("Failed to fetch user data:", error);
            setLoading(false);
        }
    }

    const navigateToProfile = () => {
        navigate("/profile");
    }

    const handleLogout = () => {
        logout();
        navigate("/login");
    }

    const menuOptions = [
        {label: "Przejdź do profilu", onClick: navigateToProfile},
        {label: "Wyloguj się", onClick: handleLogout},
    ];

    return (
        <Box className="header">
            {loading ? <CircularProgress/> :
                <>
                    <Box className="school-info">
                        <img className="school-logo" src={schoolLogo} alt="School Logo"/>
                        <Typography className="school-name">
                            {schoolName}
                        </Typography>
                    </Box>
                    {user && (
                        <Box className="user-info">
                            <Box className="user-details">
                                <Typography>Jesteś zalogowany jako: </Typography>
                                <Typography className="user-name">
                                    {`${user.name} ${user.surname}`}
                                </Typography>
                            </Box>
                            <NotificationBell/>
                            <PopupMenu options={menuOptions}>
                                <Avatar className="avatar" alt={user.name} src={user.image_base64}/>
                            </PopupMenu>
                        </Box>
                    )}
                </>}
        </Box>);
}

export default Header;
