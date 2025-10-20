import React, { useEffect, useState } from "react";
import {
    Box,
    Button,
    CircularProgress,
    Typography,
    Autocomplete,
    TextField,
    Snackbar,
    Alert,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle
} from "@mui/material";
import { Form, FormikProvider, useFormik } from "formik";
import { get, post, del } from "../../api";
import { StudentRole } from "../../views/admin/roles";
import "./EditGroups.scss";

const EditGroups = () => {
    const [allGroups, setAllGroups] = useState([]);
    const [allStudents, setAllStudents] = useState([]);
    const [initialGroupStudents, setInitialGroupStudents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");
    const [dialogOpen, setDialogOpen] = useState(false);
    const [groupToDelete, setGroupToDelete] = useState(null);

    useEffect(() => {
        const fetchInitialData = async () => {
            try {
                const [groupsRes, studentsRes] = await Promise.all([
                    get('/group/all'),
                    get('/user/all', { role: StudentRole })
                ]);
                setAllGroups(groupsRes.data);
                setAllStudents(studentsRes.data);
            } catch (error) {
                showSnackbar("Nie udało się pobrać danych początkowych", "error");
            }
        };
        fetchInitialData();
    }, []);

    const showSnackbar = (message, severity) => {
        setSnackbarMessage(message);
        setSnackbarSeverity(severity);
        setSnackbarOpen(true);
    };

    const formik = useFormik({
        initialValues: {
            group: null,
            students: [],
        },
        enableReinitialize: true,
        onSubmit: async (values, { setSubmitting }) => {
            try {
                setSubmitting(true);
                const initialStudentIds = new Set(initialGroupStudents.map(s => s.id));
                const finalStudentIds = new Set(values.students.map(s => s.id));

                const studentsToAdd = values.students.filter(s => !initialStudentIds.has(s.id));
                const studentsToRemove = initialGroupStudents.filter(s => !finalStudentIds.has(s.id));

                const addPromises = studentsToAdd.map(student =>
                    post('/student-group', { student_id: student.id, group_id: values.group.id })
                );
                const removePromises = studentsToRemove.map(student =>
                    del(`/student-group/student/${student.id}/group/${values.group.id}`)
                );

                await Promise.all([...addPromises, ...removePromises]);

                setInitialGroupStudents(values.students);
                showSnackbar("Zmiany w grupie zostały zapisane", "success");
            } catch (error) {
                showSnackbar("Wystąpił błąd podczas zapisywania zmian", "error");
            } finally {
                setSubmitting(false);
            }
        }
    });

    useEffect(() => {
        const fetchGroupStudents = async () => {
            const group = formik.values.group;
            if (group) {
                setLoading(true);
                try {
                    const res = await get(`/student-group/group/${group.id}`);
                    formik.setFieldValue('students', res.data);
                    setInitialGroupStudents(res.data);
                } catch (error) {
                    showSnackbar("Nie udało się pobrać uczniów dla tej grupy", "error");
                    formik.setFieldValue('students', []);
                    setInitialGroupStudents([]);
                } finally {
                    setLoading(false);
                }
            } else {
                formik.setFieldValue('students', []);
                setInitialGroupStudents([]);
            }
        };
        fetchGroupStudents();
    }, [formik.values.group]);

    const handleOpenDeleteDialog = () => {
        const group = formik.values.group;
        if (group) {
            setGroupToDelete(group);
            setDialogOpen(true);
        }
    };

    const handleCloseDeleteDialog = () => {
        setDialogOpen(false);
        setGroupToDelete(null);
    };

    const handleConfirmDelete = async () => {
        if (groupToDelete) {
            try {
                setLoading(true);
                await del(`/group/${groupToDelete.id}`);
                showSnackbar("Grupa została pomyślnie usunięta", "success");
                formik.resetForm();
                setAllGroups(prev => prev.filter(g => g.id !== groupToDelete.id));
            } catch (error) {
                showSnackbar("Wystąpił błąd podczas usuwania grupy", "error");
            } finally {
                setLoading(false);
                handleCloseDeleteDialog();
            }
        }
    };

    const handleDiscardChanges = () => {
        formik.setFieldValue('students', initialGroupStudents);
        showSnackbar("Zmiany zostały odrzucone", "info");
    };

    return (
        <Box className="add-students-view">
            <Typography className="title">Edytuj grupy</Typography>

            <FormikProvider value={formik}>
                <Form className="form">
                    <Autocomplete
                        options={allGroups}
                        getOptionLabel={(option) => `${option.group_code} (Rocznik: ${option.start_year})`}
                        isOptionEqualToValue={(option, value) => option.id === value.id}
                        onChange={(_, value) => formik.setFieldValue("group", value)}
                        value={formik.values.group}
                        renderInput={(params) => (
                            <TextField {...params} label="Wybierz grupę" variant="outlined" />
                        )}
                        className="input"
                    />

                    <Autocomplete
                        multiple
                        options={allStudents}
                        value={formik.values.students}
                        getOptionLabel={(option) => `${option.name} ${option.surname} (${option.email})`}
                        isOptionEqualToValue={(option, value) => option.id === value.id}
                        onChange={(_, value) => formik.setFieldValue("students", value)}
                        loading={loading}
                        disabled={!formik.values.group}
                        renderInput={(params) => (
                            <TextField
                                {...params}
                                label="Wybierz uczniów"
                                variant="outlined"
                                InputProps={{
                                    ...params.InputProps,
                                    endAdornment: (
                                        <>
                                            {loading ? <CircularProgress color="inherit" size={20} /> : null}
                                            {params.InputProps.endAdornment}
                                        </>
                                    ),
                                }}
                            />
                        )}
                        className="input student-input"
                    />

                    <Box className="button-container">
                        <Button
                            className="submit"
                            variant="contained"
                            type="submit"
                            disabled={loading || formik.isSubmitting || !formik.values.group}
                        >
                            {formik.isSubmitting ? <CircularProgress size={24} /> : "Zapisz zmiany"}
                        </Button>

                        {formik.values.group && (
                            <>
                                <Button
                                    className="discard-button"
                                    variant="outlined"
                                    onClick={handleDiscardChanges}
                                    disabled={loading || formik.isSubmitting}
                                >
                                    Odrzuć zmiany
                                </Button>
                                <Button
                                    className="delete-button"
                                    variant="contained"
                                    color="error"
                                    onClick={handleOpenDeleteDialog}
                                    disabled={loading || formik.isSubmitting}
                                >
                                    Usuń grupę
                                </Button>
                            </>
                        )}
                    </Box>
                </Form>
            </FormikProvider>

            <Snackbar
                open={snackbarOpen}
                autoHideDuration={6000}
                onClose={() => setSnackbarOpen(false)}
                anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
            >
                <Alert onClose={() => setSnackbarOpen(false)} severity={snackbarSeverity} sx={{ width: '100%' }}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>

            <Dialog open={dialogOpen} onClose={handleCloseDeleteDialog}>
                <DialogTitle>Potwierdź usunięcie</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Czy na pewno chcesz usunąć grupę "{groupToDelete?.group_code}"? Tej operacji nie można cofnąć.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDeleteDialog}>Anuluj</Button>
                    <Button onClick={handleConfirmDelete} color="error" autoFocus>Usuń</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default EditGroups;