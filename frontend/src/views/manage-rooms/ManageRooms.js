import React, {useEffect, useState} from "react";
import {
    Box,
    Button,
    CircularProgress,
    Typography,
    Snackbar,
    Alert,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    IconButton,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle
} from "@mui/material";
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import * as Yup from "yup";
import {Form, FormikProvider, useFormik} from "formik";
import {get, post, put, del} from "../../api";
import RoomCapacityInput, {RoomCapacitySchema} from "../../components/form/fields/room-capacity-input/RoomCapacityInput";
import RoomCodeInput, {RoomCodeSchema} from "../../components/form/fields/RoomCodeInput/RoomCodeInput";
import "./ManageRooms.scss";

const ManageRooms = () => {
    const [rooms, setRooms] = useState([]);
    const [loading, setLoading] = useState(false);
    const [editingRoom, setEditingRoom] = useState(null);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [roomToDelete, setRoomToDelete] = useState(null);

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    const showSnackbar = (message, severity) => {
        setSnackbarMessage(message);
        setSnackbarSeverity(severity);
        setSnackbarOpen(true);
    };

    const fetchRooms = async () => {
        setLoading(true);
        try {
            const res = await get('/room/all');
            setRooms(res.data);
        } catch (error) {
            console.error("Failed to fetch rooms:", error);
            showSnackbar("Nie udało się pobrać listy sal", "error");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchRooms();
    }, []);

    useEffect(() => {
        if (editingRoom) {
            formik.setValues({
                roomCode: editingRoom.room_code,
                capacity: editingRoom.capacity.toString(),
            });
        } else {
            formik.resetForm();
        }
    }, [editingRoom]);


    const validationSchema = Yup.object({
        roomCode: RoomCodeSchema,
        capacity: RoomCapacitySchema,
    });

    const formik = useFormik({
        initialValues: {
            roomCode: "",
            capacity: "",
        },
        validationSchema,
        enableReinitialize: true,
        onSubmit: async (values, {setSubmitting, resetForm}) => {
            const payload = {
                id: editingRoom ? editingRoom.id : null,
                room_code: values.roomCode,
                capacity: parseInt(values.capacity, 10),
            };

            try {
                if (editingRoom) {
                    await put('/room', payload);
                    showSnackbar("Sala została zaktualizowana", "success");
                } else {
                    await post('/room', payload);
                    showSnackbar("Sala została dodanya pomyślnie", "success");
                }
                resetForm();
                setEditingRoom(null);
                fetchRooms();
            } catch (error) {
                console.error("Failed to submit room:", error);
                showSnackbar("Wystąpił błąd podczas zapisu sali", "error");
            } finally {
                setSubmitting(false);
            }
        },
    });

    const handleEdit = (room) => setEditingRoom(room);
    const handleCancelEdit = () => setEditingRoom(null);

    const handleOpenDeleteDialog = (room) => {
        setRoomToDelete(room);
        setDialogOpen(true);
    };

    const handleCloseDeleteDialog = () => {
        setRoomToDelete(null);
        setDialogOpen(false);
    };

    const handleDelete = async () => {
        if (!roomToDelete) return;
        try {
            await del(`/room/${roomToDelete.id}`);
            showSnackbar("Sala została pomyślnie usunięta", "success");
            setRooms(prev => prev.filter(r => r.id !== roomToDelete.id));
        } catch (error) {
            console.error("Failed to delete room:", error);
            showSnackbar("Wystąpił błąd podczas usuwania sali", "error");
        } finally {
            handleCloseDeleteDialog();
        }
    };

    return (
        <Box className="manage-rooms-container">
            <Box className="form-section" elevation={3}>
                <Typography variant="h5" className="title">
                    {editingRoom ? "Edytuj salę" : "Dodaj nowyą salę"}
                </Typography>
                <FormikProvider value={formik}>
                    <Form className="form">
                        <RoomCodeInput label="Kod sali" name="roomCode"/>
                        <RoomCapacityInput label="Pojemność" name="capacity"/>
                        <Box className="button-container">
                            <Button variant="contained" type="submit" disabled={formik.isSubmitting}>
                                {formik.isSubmitting ? <CircularProgress size={24}/> : (editingRoom ? "Zapisz zmiany" : "Dodaj salę")}
                            </Button>
                            {editingRoom && (
                                <Button variant="outlined" onClick={handleCancelEdit}>Anuluj</Button>
                            )}
                        </Box>
                    </Form>
                </FormikProvider>
            </Box>

            <Box className="table-section" elevation={3}>
                <Typography variant="h5" className="title">Lista sal</Typography>
                {loading ? <CircularProgress/> : (
                    <TableContainer>
                        <Table stickyHeader>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Kod sali</TableCell>
                                    <TableCell align="right">Pojemność</TableCell>
                                    <TableCell align="center">Akcje</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {rooms.map((room) => (
                                    <TableRow key={room.id} hover>
                                        <TableCell>{room.room_code}</TableCell>
                                        <TableCell align="right">{room.capacity}</TableCell>
                                        <TableCell align="center" className="actions-cell">
                                            <IconButton onClick={() => handleEdit(room)} color="primary"><EditIcon/></IconButton>
                                            <IconButton onClick={() => handleOpenDeleteDialog(room)} color="error"><DeleteIcon/></IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}
            </Box>

            <Snackbar open={snackbarOpen} autoHideDuration={6000} onClose={() => setSnackbarOpen(false)} anchorOrigin={{vertical: "bottom", horizontal: "left"}}>
                <Alert onClose={() => setSnackbarOpen(false)} severity={snackbarSeverity} sx={{width: '100%'}}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>

            <Dialog open={dialogOpen} onClose={handleCloseDeleteDialog}>
                <DialogTitle>Potwierdź usunięcie</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Czy na pewno chcesz usunąć salę "{roomToDelete?.room_code}"? Tej operacji nie można cofnąć.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDeleteDialog}>Anuluj</Button>
                    <Button onClick={handleDelete} color="error" autoFocus>Usuń</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default ManageRooms;