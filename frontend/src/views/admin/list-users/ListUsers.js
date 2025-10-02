import UsersDataGrid from "../../../components/users-data-grid/UsersDataGrid";
import React, {useEffect, useState} from "react";
import {Box, Typography, Snackbar, Alert} from "@mui/material"; // Added Snackbar and Alert
import {get} from "../../../api";
import './ListUsers.scss'

function ListUsers() {

    const [users, setUsers] = useState();

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    useEffect( () => {
        fetchUsers()
    }, [])

    const fetchUsers = async () => {
        try {
            const users = await get(`/user/all`)
            setUsers(users.data)
        } catch (error) {
            console.error("Error fetching users:", error);
            setSnackbarMessage("Wystąpił błąd podczas pobierania użytkowników");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
        }
    }

    const handleSnackbarClose = (event, reason) => {
        if (reason === "clickaway") {
            return;
        }
        setSnackbarOpen(false);
    };

    return (
        <Box className="list-users">
            <Typography className="title">Users List</Typography>
            <UsersDataGrid rows = {users}/>

            <Snackbar
                open={snackbarOpen}
                autoHideDuration={6000}
                onClose={handleSnackbarClose}
                anchorOrigin={{vertical: "bottom", horizontal: "left"}}
            >
                <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{width: '100%'}}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>
        </Box>
    )
}

export default ListUsers;