import React, { useState, useEffect } from "react";
import { useFormik, FormikProvider, Form } from "formik";
import * as Yup from "yup";
import {
    Button,
    Typography,
    CircularProgress,
    Snackbar,
    Alert,
    TextField,
    Paper,
    Box
} from "@mui/material";
import { websocketClient } from "../../api";
import "./WebsocketTest.scss";

const MessageSchema = Yup.object({
    message: Yup.string().required("Wiadomość nie może być pusta"),
});

const WebsocketTest = () => {
    const [messages, setMessages] = useState([]);
    const [isConnected, setIsConnected] = useState(false);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    useEffect(() => {
        websocketClient.connect('/ws/echo');

        const handleOpen = () => {
            setMessages([{ sender: 'System', text: 'Połączono pomyślnie!' }]);
            setIsConnected(true);
        };

        const handleMessage = (data) => {
            const messageText = typeof data === 'object' ? data.text : data;
            setMessages(prev => [...prev, { sender: 'Server', text: messageText }]);
        };

        const handleError = () => {
            setSnackbarMessage("Błąd połączenia z serwerem WebSocket.");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
            setMessages(prev => [...prev, { sender: 'System', text: 'Błąd połączenia.' }]);
            setIsConnected(false);
        };

        const handleClose = () => {
            if (isConnected) {
                setMessages(prev => [...prev, { sender: 'System', text: 'Rozłączono.' }]);
            }
            setIsConnected(false);
        };

        websocketClient.on('open', handleOpen);
        websocketClient.on('message', handleMessage);
        websocketClient.on('error', handleError);
        websocketClient.on('close', handleClose);

        return () => {
            websocketClient.off('open', handleOpen);
            websocketClient.off('message', handleMessage);
            websocketClient.off('error', handleError);
            websocketClient.off('close', handleClose);
            websocketClient.disconnect();
        };
    }, []);

    const formik = useFormik({
        initialValues: {
            message: "",
        },
        validationSchema: MessageSchema,
        onSubmit: (values, { setSubmitting, resetForm }) => {
            if (isConnected) {
                try {
                    websocketClient.send({ text: values.message });
                    setMessages(prev => [...prev, { sender: 'You', text: values.message }]);
                    resetForm();
                } catch (error) {
                    setSnackbarMessage("Nie udało się wysłać wiadomości.");
                    setSnackbarSeverity("error");
                    setSnackbarOpen(true);
                }
            } else {
                setSnackbarMessage("Nie jesteś połączony z serwerem.");
                setSnackbarSeverity("warning");
                setSnackbarOpen(true);
            }
            setSubmitting(false);
        },
    });

    const handleSnackbarClose = (event, reason) => {
        if (reason === "clickaway") {
            return;
        }
        setSnackbarOpen(false);
    };

    return (
        <Box className="websocket-test">
            <Typography variant="h4" component="h1" className="title">
                Test WebSocket Echo
            </Typography>

            <Paper elevation={3} className="message-area">
                {messages.map((msg, index) => (
                    <Typography
                        key={index}
                        className={`message ${msg.sender.toLowerCase()}`}
                    >
                        <strong>{msg.sender}:</strong> {msg.text}
                    </Typography>
                ))}
            </Paper>

            <FormikProvider value={formik}>
                <Form className="form">
                    <TextField
                        fullWidth
                        id="message"
                        name="message"
                        label="Wpisz wiadomość"
                        value={formik.values.message}
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        error={formik.touched.message && Boolean(formik.errors.message)}
                        helperText={formik.touched.message && formik.errors.message}
                        variant="outlined"
                        autoComplete="off"
                    />
                    <Button
                        className="submit"
                        variant="contained"
                        color="primary"
                        type="submit"
                        disabled={formik.isSubmitting || !isConnected}
                    >
                        {formik.isSubmitting ? (
                            <CircularProgress size={24} />
                        ) : (
                            "Wyślij"
                        )}
                    </Button>
                </Form>
            </FormikProvider>

            <Snackbar
                open={snackbarOpen}
                autoHideDuration={6000}
                onClose={handleSnackbarClose}
                anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
            >
                <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{ width: '100%' }}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default WebsocketTest;

