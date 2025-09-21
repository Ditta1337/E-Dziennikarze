import {Alert, Box, Button, CircularProgress, Divider, Snackbar, Typography} from "@mui/material";
import List from '@mui/material/List';
import React, {Fragment, useEffect, useState} from "react";
import AttendanceRow from "../../components/attendance-row/AttendanceRow";
import {post} from "../../api.js"
import "./Attendance.scss"

const dummyGroup = {
    "group_id": 123,
    "group_name": "4D",
    "students": [
        {
            "id": 101,
            "name": "Jan",
            "surname": "Kowalski",
            "image_base64": "https://stoppasozytom.pl/img/asset/bWFpbi9hcnR5a3VseS9waWVyd3N6eS1waWVzL3BpZXJ3c3p5LXBpZXMtby1jenltLXBhbWlldGFjLXNtYWxsLmpwZw==?w=760&h=427&fit=crop&s=3d6245a5f13aadd7c4edb31679df49bd",
            "status": "ABSENT"
        },
        {
            "id": 102,
            "name": "Anna",
            "surname": "Nowak",
            "image_base64": "https://sklepzkarmami.pl/poradnik/wp-content/uploads/2022/08/aidi-scaled.jpg",
            "status": "PRESENT"

        },
        {
            "id": 103,
            "name": "Piotr",
            "surname": "Zieliński",
            "image_base64": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcThmIFs-N4PT0D3gxuDINqgkKWhSxR6sNwJ6g&s",
            "status": "ABSENCE_SCHOOL_TRIP"
        },
        {
            "id": 104,
            "name": "Maria",
            "surname": "Wiśniewska",
            "image_base64": "https://randomuser.me/api/portraits/women/65.jpg",
            "status": null
        },
        {
            "id": 105,
            "name": "Krzysztof",
            "surname": "Lewandowski",
            "image_base64": "https://randomuser.me/api/portraits/men/32.jpg",
            "status": null
        },
        {
            "id": 106,
            "name": "Agnieszka",
            "surname": "Dąbrowska",
            "image_base64": "https://randomuser.me/api/portraits/women/12.jpg",
            "status": null
        },
        {
            "id": 107,
            "name": "Michał",
            "surname": "Wójcik",
            "image_base64": "https://randomuser.me/api/portraits/men/18.jpg",
            "status": null
        },
        {
            "id": 108,
            "name": "Karolina",
            "surname": "Kozłowska",
            "image_base64": "https://randomuser.me/api/portraits/women/56.jpg",
            "status": null
        },
        {
            "id": 109,
            "name": "Tomasz",
            "surname": "Jankowski",
            "image_base64": "https://randomuser.me/api/portraits/men/41.jpg",
            "status": null
        },
        {
            "id": 110,
            "name": "Magdalena",
            "surname": "Mazur",
            "image_base64": "https://randomuser.me/api/portraits/women/23.jpg",
            "status": null
        },
        {
            "id": 111,
            "name": "Paweł",
            "surname": "Krawczyk",
            "image_base64": "https://randomuser.me/api/portraits/men/77.jpg",
            "status": null
        },
        {
            "id": 112,
            "name": "Ewa",
            "surname": "Grabowska",
            "image_base64": "https://randomuser.me/api/portraits/women/80.jpg",
            "status": null
        }
    ]

}

const Attendance = () => {
    const [groupData, setGroupData] = useState();
    const [loadingGroupData, setLoadingGroupData] = useState(true);
    const [attendanceData, setAttendanceData] = useState();
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    const fetchData = async () => {//TODO connect to back
        await new Promise(r => setTimeout(r, 500))
        setGroupData(dummyGroup);
        setAttendanceData(buildAttendancePayload(dummyGroup))
        setLoadingGroupData(false);
    }

    const buildAttendancePayload = (groupData) => {
        return {
            group_id: groupData.group_id,
            students: groupData.students.map(student => ({
                id: student.id,
                status: student.status || null
            }))
        }
    }

    const onStatusChange = (id, newStatus) => {
        setAttendanceData(prevData => ({
            ...prevData,
            students: prevData.students.map(student => student.id === id ? {...student, status: newStatus} : student),
        }))
        console.log(newStatus)
        console.log(attendanceData)
    }

    const submitAttendance = async () => {
        if (!attendanceData) return;

        console.log(attendanceData)

        try {
            await post("/attendance", attendanceData) //TODO: replace with a real uri

            setSnackbarSeverity("success")
            setSnackbarMessage("Obecność została zapisana!")
            setSnackbarOpen(true)
        } catch (error) {
            setSnackbarSeverity("error")
            setSnackbarMessage("Nie udało się zapisać obecności.")
            setSnackbarOpen(true)
            console.error("Submit attendance error:", error)
        }
    }

    const handleSnackbarClose = (event, reason) => {
        if (reason === "clickaway") {
            return;
        }
        setSnackbarOpen(false);
    };

    useEffect(() => {
        fetchData()
    }, []);

    return (
        <Box className="attendance">
            {loadingGroupData ? (
                <CircularProgress/>
            ) : (
                <>
                    <Typography className="group-name">
                        {`Obecność grupy: ${groupData.group_name}`}
                    </Typography>
                    <List className="list">
                        {groupData.students.map((student, index) => (
                            <Fragment key={student.id || index}>
                                <AttendanceRow studentData={student} onStatusChange={onStatusChange}/>
                                {index < groupData.students.length - 1 && (
                                    <Divider variant="middle" component="li"/>
                                )}
                            </Fragment>
                        ))}
                    </List>
                    <Button
                        className="save"
                        variant="contained"
                        onClick={submitAttendance}>
                        Zapisz
                    </Button>
                </>
            )}
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
    );

}

export default Attendance