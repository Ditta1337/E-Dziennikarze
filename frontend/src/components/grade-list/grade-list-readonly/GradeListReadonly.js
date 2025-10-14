import React, {useEffect, useState, useCallback} from 'react';
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
import {get} from '../../../api';
import './GradeListReadonly.scss';

function GradeListReadonly({studentId, title = "Twoje Oceny"}) {
    const [gradesBySubject, setGradesBySubject] = useState({});
    const [averages, setAverages] = useState([]);
    const [finalAverage, setFinalAverage] = useState(null);
    const [loading, setLoading] = useState(true);

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    const fetchGrades = useCallback(async () => {
        if (!studentId) return;
        setLoading(true);
        try {
            const [
                gradesRes,
                finalGradesRes,
                finalAveragesRes,
                averageGradesRes
            ] = await Promise.all([
                get(`/grade/all/student/${studentId}`),
                get(`/grade/final/student/${studentId}`),
                get(`/grade/final/average/student/${studentId}`),
                get(`/grade/average/student/${studentId}`)
            ]);

            const regularGrades = gradesRes.data;
            const finalGrades = finalGradesRes.data;
            const finalGradesWithFlag = finalGrades.map(grade => ({ ...grade, is_final: true }));
            const allGrades = [...regularGrades, ...finalGradesWithFlag];

            setFinalAverage(finalAveragesRes.data);
            setAverages(averageGradesRes.data);

            const grouped = allGrades.reduce((acc, grade) => {
                const {subject_id: subjectId, subject_name: subjectName} = grade;

                if (!acc[subjectId]) {
                    acc[subjectId] = {
                        subjectName: subjectName || 'Inny',
                        grades: [],
                        finalGrade: null
                    };
                }

                if (grade.is_final) {
                    acc[subjectId].finalGrade = grade;
                } else {
                    acc[subjectId].grades.push(grade);
                }
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
            ) : (
                <>
                    {typeof finalAverage === 'number' && (
                        <Typography variant="h6" className="final-average">
                            Średnia końcowa ze wszystkich przedmiotów: <strong>{finalAverage.toFixed(2)}</strong>
                        </Typography>
                    )}

                    {Object.keys(gradesBySubject).length === 0 ? (
                        <Typography sx={{ mt: 2 }}>Nie masz jeszcze żadnych ocen.</Typography>
                    ) : (
                        <Box className="subjects-container">
                            {Object.entries(gradesBySubject).map(([subjectId, data]) => {
                                const subjectAverage = averages.find(avg => avg.subject_id === subjectId);

                                return (
                                    <Accordion key={subjectId} className="subject-accordion">
                                        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                                            <Box sx={{ display: 'flex', justifyContent: 'space-between', width: '100%', alignItems: 'center', pr: 1 }}>
                                                <Typography className="subject-name">{data.subjectName}</Typography>

                                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 3 }}>
                                                    {subjectAverage && (
                                                        <Typography className="subject-average" sx={{ color: 'text.secondary', fontWeight: '500' }}>
                                                            Średnia: {subjectAverage.average.toFixed(2)}
                                                        </Typography>
                                                    )}
                                                    {data.finalGrade && (
                                                        <Typography className="subject-final-grade" sx={{ fontWeight: 'bold', color: 'primary.main' }}>
                                                            Ocena Końcowa: {data.finalGrade.grade}
                                                        </Typography>
                                                    )}
                                                </Box>
                                            </Box>
                                        </AccordionSummary>
                                        <AccordionDetails>
                                            <List>
                                                {data.grades.length > 0 ? (
                                                    data.grades.map((grade) => (
                                                        <ListItem key={grade.id} divider>
                                                            <ListItemText
                                                                primary={`Ocena: ${grade.grade}`}
                                                                secondary={`Waga: ${grade.weight} | Data: ${new Date(grade.created_at).toLocaleDateString()}`}
                                                            />
                                                        </ListItem>
                                                    ))
                                                ) : (
                                                    <Typography variant="body2" sx={{color: 'text.secondary', pl: 2}}>
                                                        Brak ocen cząstkowych.
                                                    </Typography>
                                                )}
                                            </List>
                                        </AccordionDetails>
                                    </Accordion>
                                );
                            })}
                        </Box>
                    )}
                </>
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