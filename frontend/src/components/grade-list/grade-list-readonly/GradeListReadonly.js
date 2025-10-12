import React, { useEffect, useState, useCallback } from 'react';
import {
    Box,
    Typography,
    Accordion,
    AccordionSummary,
    AccordionDetails,
    List,
    ListItem,
    ListItemText,
    Snackbar,
    Alert,
    CircularProgress
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { get } from '../../../api';
import './GradeListReadonly.scss';

function GradeListReadonly({studentId, title = "Twoje Oceny"}) {
    const [gradesBySubject, setGradesBySubject] = useState({});
    const [loading, setLoading] = useState(true);

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    const fetchGrades = useCallback(async () => {
        if (!studentId) return;
        setLoading(true);
        try {
            const response = await get(`/grade/all/student/${studentId}`);
            const grades = response.data;

            const grouped = grades.reduce((acc, grade) => {
                const { subject_id: subjectId, subject_name: subjectName } = grade;

                if (!acc[subjectId]) {
                    acc[subjectId] = { subjectName: subjectName, grades: [] };
                }
                acc[subjectId].grades.push(grade);
                return acc;
            }, {});

            setGradesBySubject(grouped);
        } catch (error) {
            console.error("Error fetching grades:", error);
            setSnackbarMessage("Wystąpił błąd podczas pobierania ocen");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
        } finally {
            setLoading(false);
        }
    }, [studentId]);

    useEffect(() => {
        fetchGrades();
    }, [fetchGrades]);

    const handleSnackbarClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        setSnackbarOpen(false);
    };

    return (
        <Box className="grade-list-student">
            <Typography className="title">{title}</Typography>

            {loading ? (
                <CircularProgress />
            ) : Object.keys(gradesBySubject).length === 0 ? (
                <Typography>Nie masz jeszcze żadnych ocen.</Typography>
            ) : (
                <Box className="subjects-container">
                    {Object.entries(gradesBySubject).map(([subjectId, data]) => (
                        <Accordion key={subjectId} className="subject-accordion">
                            <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                                <Typography className="subject-name">{data.subjectName}</Typography>
                            </AccordionSummary>
                            <AccordionDetails>
                                <List>
                                    {data.grades.map((grade) => (
                                        <ListItem key={grade.id} divider>
                                            <ListItemText
                                                primary={`Ocena: ${grade.grade}`}
                                                secondary={`Waga: ${grade.weight} | Data: ${new Date(grade.created_at).toLocaleDateString()}`}
                                            />
                                        </ListItem>
                                    ))}
                                </List>
                            </AccordionDetails>
                        </Accordion>
                    ))}
                </Box>
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

export default GradeListReadonly;