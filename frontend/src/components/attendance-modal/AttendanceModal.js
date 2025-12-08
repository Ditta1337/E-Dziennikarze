import {Alert, Box, Button, CircularProgress, Divider, Snackbar, Typography} from "@mui/material";
import List from '@mui/material/List';
import React, {Fragment, useEffect, useState} from "react";
import AttendanceRow from "./attendance-row/AttendanceRow";
import {get, post} from "../../api.js"
import "./AttendanceModal.scss"
import Modal from "../modal/Modal";
import {presentValue} from "./attendance-row/attendance-controls/attendanceTypeMap";

const AttendanceModal = ({isOpen, onClose, groupName, groupId, subjectId, assignedLessonId}) => {
    const [groupStudents, setGroupStudents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [attendances, setAttendances] = useState([]);
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

    const makeAllPresentAttendance = () => {
        const updatedExisting = attendances.map(att => ({
            ...att,
            status: presentValue
        }));
        const newAttendances = groupStudents
            .filter(student => !findAttendanceByStudentId(student.id))
            .map(student => ({
                lesson_id: assignedLessonId,
                student_id: student.id,
                subject_id: subjectId,
                status: presentValue
            }));
        return [...updatedExisting, ...newAttendances];
    };

    const submitAttendances = async () => {
        try{
            const updatedAttendances = makeAllPresentAttendance()
            console.log(updatedAttendances)
            const attendanceData = await post(`/attendance/many`, updatedAttendances)
            setAttendances(attendanceData.data)
        } catch(error) {
            console.error("error")
            setSnackbarSeverity("error")
            setSnackbarMessage("Nie udało się przesłać danych o obecności")
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

    const handleAllPresent = async () => {
        setLoading(true)
        await submitAttendances()
        setLoading(false)
    }

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
                        <Fragment>
                            <Box className="all-present">
                                <Button
                                    size="small"
                                    variant="contained"
                                    color="success"
                                    onClick={handleAllPresent}
                                >
                                    Wszyscy obecni
                                </Button>
                            </Box>
                        </Fragment>
                        {groupStudents.map((student, index) => (
                            <Fragment key={student.id || index}>
                                <AttendanceRow studentData={student} lessonId={assignedLessonId} subjectId={subjectId}
                                               existingAttendanceData={findAttendanceByStudentId(student.id)}/>
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