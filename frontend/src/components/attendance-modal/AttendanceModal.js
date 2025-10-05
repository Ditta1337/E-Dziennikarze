import {Alert, CircularProgress, Divider, Snackbar, Typography} from "@mui/material";
import List from '@mui/material/List';
import React, {Fragment, useEffect, useState} from "react";
import AttendanceRow from "./attendance-row/AttendanceRow";
import {get} from "../../api.js"
import "./AttendanceModal.scss"
import Modal from "../modal/Modal";

const AttendanceModal = ({isOpen, onClose, groupName, groupId, subjectId, assignedLessonId}) => {
    const [groupStudents, setGroupStudents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [attendances, setAttendances] = useState();
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    useEffect(() => {
        if (!groupId || !assignedLessonId) {
            return;
        }

        fetchGroupUsers().then(
            fetchAttendanceData()
        )

    }, [groupId, assignedLessonId]);


    const fetchGroupUsers = async () => {
        try {
            const usersData = await get(`student-group/group/${groupId}`);
            setGroupStudents(usersData.data);
        } catch (error) {
            console.error("Fetch group users error:", error);
            setSnackbarSeverity("error")
            setSnackbarMessage("Nie udało się pobrać listy studentów.")
            setSnackbarOpen(true)
        }
    }

    const fetchAttendanceData = async () => {
        try {
            const attendanceData = await get(`attendance/lesson/${assignedLessonId}`);
            setAttendances(attendanceData.data);
            setLoading(false)
        } catch (error) {
            console.error("Fetch attendance data error:", error);
            setSnackbarSeverity("error")
            setSnackbarMessage("Nie udało się pobrać danych obecności.")
            setSnackbarOpen(true)
        }
    }

    const findAttendanceByStudentId = (studentId) => {
        if (attendances) {
            return attendances.find(attendance => attendance.student_id === studentId) || null;
        }
        return null;
    }

    const handleSnackbarClose = (event, reason) => {
        if (reason === "clickaway") {
            return;
        }
        setSnackbarOpen(false);
    };

    return (
        <Modal className="attendance"
               isOpen={isOpen}
               onClose={onClose}>
            {loading ? (
                <CircularProgress/>
            ) : (
                <>
                    <Typography className="group-name">
                        {`Obecność grupy: ${groupName}`}
                    </Typography>
                    <List className="list">
                        {groupStudents.map((student, index) => (
                            <Fragment key={student.id || index}>
                                <AttendanceRow studentData={student} lessonId={assignedLessonId} subjectId={subjectId} existingAttendanceData={findAttendanceByStudentId(student.id)}/>
                                {index < groupStudents.length - 1 && (
                                    <Divider variant="middle" component="li"/>
                                )}
                            </Fragment>
                        ))}
                    </List>
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
        </Modal>
    );

}

export default AttendanceModal