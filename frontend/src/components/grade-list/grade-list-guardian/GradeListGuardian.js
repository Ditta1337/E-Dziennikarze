import React, { useEffect, useState, useCallback } from 'react';
import {
    Box,
    Typography,
    Snackbar,
    Alert,
    CircularProgress,
    FormControl,
    InputLabel,
    Select,
    MenuItem
} from '@mui/material';
import { get } from '../../../api';
import { useStore } from "../../../store";
import './GradeListGuardian.scss';
import GradeListReadonly from "../grade-list-readonly/GradeListReadonly";

function GradeListGuardian() {
    const guardianId = useStore((state) => state.user.userId);
    const [students, setStudents] = useState([]);
    const [selectedStudentId, setSelectedStudentId] = useState('');
    const [loading, setLoading] = useState(true);

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    const fetchStudentsForGuardian = useCallback(async () => {
        if (!guardianId) {
            setLoading(false);
            return;
        }
        setLoading(true);
        try {
            const response = await get(`/student-guardian/guardian/${guardianId}`);
            const studentData = response.data;
            setStudents(studentData);

            if (studentData.length > 0) {
                setSelectedStudentId(studentData[0].id);
            }
        } catch (error) {
            console.error("Error fetching students for guardian:", error);
            setSnackbarMessage("Wystąpił błąd podczas pobierania listy uczniów");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
        } finally {
            setLoading(false);
        }
    }, [guardianId]);

    useEffect(() => {
        fetchStudentsForGuardian();
    }, [fetchStudentsForGuardian]);

    const handleStudentChange = (event) => {
        setSelectedStudentId(event.target.value);
    };

    const handleSnackbarClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        setSnackbarOpen(false);
    };

    return (
        <Box className="grade-list-guardian">
            <Typography className="title">Oceny Ucznia</Typography>

            {loading ? (
                <CircularProgress />
            ) : students.length > 0 ? (
                <>
                    <FormControl className="user-input">
                        <InputLabel id="student-select-label">Wybierz ucznia</InputLabel>
                        <Select
                            labelId="student-select-label"
                            value={selectedStudentId}
                            label="Wybierz ucznia"
                            onChange={handleStudentChange}
                        >
                            {students.map((student) => (
                                <MenuItem key={student.id} value={student.id}>
                                    {`${student.name} ${student.surname}`}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    {selectedStudentId && <GradeListReadonly studentId={selectedStudentId} title="" />}
                </>
            ) : (
                <Typography>Nie przypisano do Ciebie żadnych uczniów.</Typography>
            )}

            <Snackbar
                open={snackbarOpen}
                autoHideDuration={6000}
                onClose={handleSnackbarClose}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
            >
                <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{ width: '100%' }}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>
        </Box>
    );
}

export default GradeListGuardian;