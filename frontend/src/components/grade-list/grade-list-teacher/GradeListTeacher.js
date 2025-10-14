import React, {useEffect, useState, useCallback} from 'react';
import {
    Box, Typography, Button, Snackbar, Alert, CircularProgress, Dialog,
    DialogTitle, DialogContent, TextField, DialogActions, IconButton, Chip,
    FormControl, InputLabel, Select, MenuItem
} from '@mui/material';
import {DataGrid} from '@mui/x-data-grid';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import {get, post, put, del} from '../../../api';
import './GradeListTeacher.scss';

const gradeOptions = [1, 2, 3, 3.5, 4, 4.5, 5, 6];

function GradeListTeacher({groupId, subjectId}) {
    const [rows, setRows] = useState([]);
    const [groupStudents, setGroupStudents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [modal, setModal] = useState({open: false, mode: 'add', data: {}, studentName: ''});
    const [deleteConfirm, setDeleteConfirm] = useState({open: false, gradeId: null});
    const [finalGradeModal, setFinalGradeModal] = useState({open: false, mode: 'add', data: {}, studentName: ''});
    const [deleteFinalGradeConfirm, setDeleteFinalGradeConfirm] = useState({open: false, gradeId: null});
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    const fetchData = useCallback(async () => {
        if (!groupId || !subjectId) return;
        setLoading(true);
        try {
            const [studentsRes, gradesRes, finalGradeRes, averageGradesRes] = await Promise.all([
                get(`student-group/group/${groupId}`),
                get(`/grade/all/group/${groupId}/subject/${subjectId}`),
                get(`/grade/final/group/${groupId}/subject/${subjectId}`),
                get(`/grade/average/group/${groupId}/subject/${subjectId}`)
            ]);
            const students = studentsRes.data;
            const grades = gradesRes.data;
            const finalGrades = finalGradeRes.data;
            const averageGrades = averageGradesRes.data;
            setGroupStudents(students);
            const dataRows = students.map(student => ({
                id: student.id,
                studentName: `${student.name} ${student.surname}`,
                grades: grades.filter(g => g.student_id === student.id),
                averageGrade: averageGrades.find(ag => ag.student_id === student.id)?.average || 0.0,
                finalGrade: finalGrades.find(fg => fg.student_id === student.id) || null
            }));
            setRows(dataRows);
        } catch (error) {
            console.error("Error fetching data:", error);
            setSnackbarSeverity("error");
            setSnackbarMessage("Wystąpił błąd podczas pobierania danych.");
            setSnackbarOpen(true);
        } finally {
            setLoading(false);
        }
    }, [groupId, subjectId]);

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    const handleModalOpen = (mode, data = {}) => {
        const studentId = data.student_id || data.studentId;
        const student = groupStudents.find(s => s.id === studentId);
        const studentName = student ? `${student.name} ${student.surname}` : '';
        setModal({open: true, mode, data, studentName});
    };

    const handleModalClose = () => {
        setModal({open: false, mode: 'add', data: {}, studentName: ''});
    };

    const handleModalInputChange = (event) => {
        const {name, value} = event.target;
        setModal(prev => ({...prev, data: {...prev.data, [name]: value}}));
    };

    const handleModalSave = async () => {
        const studentId = modal.data.student_id || modal.data.studentId;
        const {id, grade, weight} = modal.data;

        if (!grade || !weight || !studentId) {
            setSnackbarMessage("Ocena, waga i student są wymagane.");
            setSnackbarSeverity("warning");
            setSnackbarOpen(true);
            return;
        }

        const weightValue = parseFloat(weight);
        if (isNaN(weightValue) || weightValue < 0 || weightValue > 1) {
            setSnackbarMessage("Waga musi być liczbą od 0 do 1.");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
            return;
        }

        const payload = {
            student_id: studentId,
            subject_id: subjectId,
            grade: parseFloat(grade),
            weight: weightValue,
            final: false
        };

        try {
            if (modal.mode === 'add') {
                await post('/grade', payload);
                setSnackbarMessage('Ocena dodana pomyślnie');
                setSnackbarSeverity('success');
            } else {
                await put('/grade', {...payload, id});
                setSnackbarMessage('Ocena zaktualizowana pomyślnie');
                setSnackbarSeverity('success');
            }
            fetchData();
        } catch (error) {
            setSnackbarMessage('Wystąpił błąd podczas zapisu oceny');
            setSnackbarSeverity('error');
        } finally {
            setSnackbarOpen(true);
            handleModalClose();
        }
    };

    const handleDeleteRequest = async () => {
        try {
            await del(`/grade?gradeId=${deleteConfirm.gradeId}`);
            setSnackbarMessage('Ocena usunięta pomyślnie');
            setSnackbarSeverity('success');
            fetchData();
        } catch (error) {
            setSnackbarMessage('Wystąpił błąd podczas usuwania oceny');
            setSnackbarSeverity('error');
        } finally {
            setSnackbarOpen(true);
            setDeleteConfirm({open: false, gradeId: null});
        }
    };

    const handleDeleteFromModal = () => {
        const gradeId = modal.data.id;
        handleModalClose();
        setDeleteConfirm({open: true, gradeId: gradeId});
    };

    const handleFinalGradeModalOpen = (mode, data = {}) => {
        const studentId = data.student_id || data.studentId;
        const student = groupStudents.find(s => s.id === studentId);
        const studentName = student ? `${student.name} ${student.surname}` : '';
        setFinalGradeModal({open: true, mode, data, studentName});
    };

    const handleFinalGradeModalClose = () => {
        setFinalGradeModal({open: false, mode: 'add', data: {}, studentName: ''});
    };

    const handleFinalGradeModalInputChange = (event) => {
        const {name, value} = event.target;
        setFinalGradeModal(prev => ({...prev, data: {...prev.data, [name]: value}}));
    };

    const handleFinalGradeModalSave = async () => {
        const {id, grade, student_id, studentId} = finalGradeModal.data;
        const currentStudentId = student_id || studentId;

        if (!grade || !currentStudentId) {
            setSnackbarMessage("Ocena i student są wymagani.");
            setSnackbarSeverity("warning");
            setSnackbarOpen(true);
            return;
        }

        const payload = {
            student_id: currentStudentId,
            subject_id: subjectId,
            grade: parseFloat(grade),
            weight: 1.0,
            final: true
        };

        try {
            if (finalGradeModal.mode === 'add') {
                await post('/grade', payload);
                setSnackbarMessage('Ocena końcowa dodana pomyślnie');
                setSnackbarSeverity('success');
            } else {
                await put('/grade', {...payload, id});
                setSnackbarMessage('Ocena końcowa zaktualizowana pomyślnie');
                setSnackbarSeverity('success');
            }
            fetchData();
        } catch (error) {
            setSnackbarMessage('Wystąpił błąd podczas zapisu oceny końcowej');
            setSnackbarSeverity('error');
        } finally {
            setSnackbarOpen(true);
            handleFinalGradeModalClose();
        }
    };

    const handleDeleteFinalGradeFromModal = () => {
        const gradeId = finalGradeModal.data.id;
        handleFinalGradeModalClose();
        setDeleteFinalGradeConfirm({open: true, gradeId: gradeId});
    };

    const handleDeleteFinalGradeRequest = async () => {
        try {
            await del(`/grade/final?finalGradeId=${deleteFinalGradeConfirm.gradeId}`);
            setSnackbarMessage('Ocena końcowa usunięta pomyślnie');
            setSnackbarSeverity('success');
            fetchData();
        } catch (error) {
            setSnackbarMessage('Wystąpił błąd podczas usuwania oceny końcowej');
            setSnackbarSeverity('error');
        } finally {
            setSnackbarOpen(true);
            setDeleteFinalGradeConfirm({open: false, gradeId: null});
        }
    };

    const handleSnackbarClose = (event, reason) => {
        if (reason === 'clickaway') return;
        setSnackbarOpen(false);
    };

    const columns = [
        {field: 'studentName', headerName: 'Student', flex: 1, minWidth: 150},
        {
            field: 'grades',
            headerName: 'Oceny',
            flex: 3,
            minWidth: 350,
            sortable: false,
            renderCell: (params) => (
                <Box className="grades-cell">
                    {params.value.map(grade => (
                        <Chip
                            key={grade.id}
                            label={`${grade.grade} (${grade.weight})`}
                            onClick={() => handleModalOpen('edit', grade)}
                            size="small"
                            sx={{mr: 0.5, cursor: 'pointer'}}
                        />
                    ))}
                    <IconButton size="small" title="Dodaj ocenę"
                                onClick={() => handleModalOpen('add', {studentId: params.id, weight: 1.0})}>
                        <AddCircleOutlineIcon/>
                    </IconButton>
                </Box>
            )
        },
        {
            field: "averageGrade",
            headerName: "Średnia",
            flex: 1,
            minWidth: 100,
            renderCell: (params) => (
                <Chip
                    label={params.value.toFixed(2)}
                    size="small"
                    sx={{cursor: 'default'}}
                />
            )
        },
        {
            field: "finalGrade",
            headerName: "Ocena Końcowa",
            flex: 1,
            minWidth: 150,
            sortable: false,
            renderCell: (params) => (
                params.value ? (
                    <Chip
                        label={params.value.grade}
                        onClick={() => handleFinalGradeModalOpen('edit', params.value)}
                        size="small"
                        sx={{cursor: 'pointer'}}
                    />
                ) : (
                    <IconButton
                        size="small"
                        title="Dodaj ocenę końcową"
                        onClick={() => handleFinalGradeModalOpen('add', {studentId: params.id})}
                    >
                        <AddCircleOutlineIcon/>
                    </IconButton>
                )
            )
        }
    ];

    return (
        <Box className="grade-list-teacher">
            <Typography className="title">Zarządzanie Ocenami</Typography>
            {loading ? <CircularProgress/> : (
                <DataGrid
                    rows={rows}
                    columns={columns}
                    autoHeight
                    initialState={{pagination: {paginationModel: {pageSize: 10}}}}
                    pageSizeOptions={[10, 25, 50]}
                    disableRowSelectionOnClick
                />
            )}

            <Dialog open={modal.open} onClose={handleModalClose}>
                <DialogTitle sx={{textAlign: 'center'}}>
                    {modal.mode === 'add' ? 'Dodaj Ocenę dla' : 'Edytuj Ocenę dla'} {modal.studentName}
                </DialogTitle>
                <DialogContent>
                    <FormControl fullWidth margin="dense">
                        <InputLabel id="grade-select-label">Ocena</InputLabel>
                        <Select
                            labelId="grade-select-label"
                            name="grade"
                            value={modal.data.grade || ''}
                            label="Ocena"
                            onChange={handleModalInputChange}
                        >
                            {gradeOptions.map(option => (
                                <MenuItem key={option} value={option}>{option}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <TextField
                        margin="dense"
                        label="Waga"
                        type="number"
                        fullWidth
                        name="weight"
                        value={modal.data.weight || ''}
                        onChange={handleModalInputChange}
                        inputProps={{step: "0.1", min: "0", max: "1"}}
                    />
                </DialogContent>
                <DialogActions>
                    {modal.mode === 'edit' && (
                        <Button onClick={handleDeleteFromModal} color="error" sx={{mr: 'auto'}}>
                            Usuń
                        </Button>
                    )}
                    <Button onClick={handleModalClose}>Anuluj</Button>
                    <Button onClick={handleModalSave}>Zapisz</Button>
                </DialogActions>
            </Dialog>

            <Dialog open={deleteConfirm.open} onClose={() => setDeleteConfirm({open: false, gradeId: null})}>
                <DialogTitle>Potwierdź Usunięcie</DialogTitle>
                <DialogContent>
                    <Typography>Czy na pewno chcesz usunąć tę ocenę?</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDeleteConfirm({open: false, gradeId: null})}>Anuluj</Button>
                    <Button onClick={handleDeleteRequest} color="error">Usuń</Button>
                </DialogActions>
            </Dialog>

            <Dialog open={finalGradeModal.open} onClose={handleFinalGradeModalClose}>
                <DialogTitle sx={{textAlign: 'center'}}>
                    {finalGradeModal.mode === 'add' ? 'Dodaj Ocenę Końcową dla' : 'Edytuj Ocenę Końcową dla'} {finalGradeModal.studentName}
                </DialogTitle>
                <DialogContent>
                    <FormControl fullWidth margin="dense">
                        <InputLabel id="final-grade-select-label">Ocena Końcowa</InputLabel>
                        <Select
                            labelId="final-grade-select-label"
                            name="grade"
                            value={finalGradeModal.data.grade || ''}
                            label="Ocena Końcowa"
                            onChange={handleFinalGradeModalInputChange}
                        >
                            {gradeOptions.map(option => (
                                <MenuItem key={option} value={option}>{option}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </DialogContent>
                <DialogActions>
                    {finalGradeModal.mode === 'edit' && (
                        <Button onClick={handleDeleteFinalGradeFromModal} color="error" sx={{mr: 'auto'}}>
                            Usuń
                        </Button>
                    )}
                    <Button onClick={handleFinalGradeModalClose}>Anuluj</Button>
                    <Button onClick={handleFinalGradeModalSave}>Zapisz</Button>
                </DialogActions>
            </Dialog>

            <Dialog open={deleteFinalGradeConfirm.open}
                    onClose={() => setDeleteFinalGradeConfirm({open: false, gradeId: null})}>
                <DialogTitle>Potwierdź Usunięcie Oceny Końcowej</DialogTitle>
                <DialogContent>
                    <Typography>Czy na pewno chcesz usunąć tę ocenę końcową?</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDeleteFinalGradeConfirm({open: false, gradeId: null})}>Anuluj</Button>
                    <Button onClick={handleDeleteFinalGradeRequest} color="error">Usuń</Button>
                </DialogActions>
            </Dialog>

            <Snackbar open={snackbarOpen} autoHideDuration={6000} onClose={handleSnackbarClose}
                      anchorOrigin={{vertical: 'bottom', horizontal: 'left'}}>
                <Alert onClose={handleSnackbarClose} severity={snackbarSeverity}
                       sx={{width: '100%'}}>{snackbarMessage}</Alert>
            </Snackbar>
        </Box>
    );
}

export default GradeListTeacher;