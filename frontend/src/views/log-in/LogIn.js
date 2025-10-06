import React, {useEffect, useState} from "react";
import "./LogIn.scss";
import * as Yup from "yup";
import {Form, useFormik, FormikProvider} from "formik";
import {Alert, Box, Button, CircularProgress, Snackbar, Typography} from "@mui/material";
import {useNavigate} from "react-router";
import {jwtDecode} from "jwt-decode";
import {post, get} from "../../api"
import {useStore} from "../../store";
import EmailInput, {EmailSchema} from "../../components/form/fields/email-input/EmailInput";
import PasswordInput from "../../components/form/fields/password-input/PasswordInput";

function LogIn() {
    const navigate = useNavigate();
    const {setUser, setToken, setRefresh, logout} = useStore();

    const [schoolName, setSchoolName] = useState("Szkoła");
    const [schoolLogo, setSchoolLogo] = useState("");
    const [loading, setLoading] = useState(true);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    useEffect(() => {
        logout()
        fetchSchoolData();
    }, []);

    const fetchSchoolData = async () => {
        try {
            const [schoolNameData, schoolImageData] = await Promise.all([
                get("/property/name/schoolFullName"),
                get("/property/name/schoolLogoBase64")]
            );
            setSchoolName(schoolNameData.data.value);
            setSchoolLogo(schoolImageData.data.value);
            setLoading(false);
        } catch (error) {
            console.error("Failed to fetch school data:", error);
        }
    }

    const validationSchema = Yup.object({
        email: EmailSchema,
        password: Yup.string().required("Hasło jest wymagane"),
    });

    const handleSnackbarClose = (event, reason) => {
        if (reason === "clickaway") {
            return;
        }
        setSnackbarOpen(false);
    };

    const formik = useFormik({
        initialValues: {
            email: "",
            password: "",
        },
        validationSchema,
        onSubmit: async (values, {setSubmitting}) => {
            try {
                const response = await post("/auth/login", {
                    email: values.email,
                    password: values.password,
                });

                const {access_token, refresh_token} = response.data;
                const decodedToken = jwtDecode(access_token);
                const userData = {
                    email: decodedToken.sub,
                    role: decodedToken.role,
                    userId: decodedToken.userId
                };

                setToken(access_token);
                setRefresh(refresh_token);
                setUser(userData);

                navigate("/gradebook");

            } catch (error) {
                console.error("Login failed:", error);
                const message = error.response?.status === 401
                    ? "Nieprawidłowy email lub hasło"
                    : "Wystąpił błąd podczas logowania";
                setSnackbarMessage(message);
                setSnackbarSeverity("error");
                setSnackbarOpen(true);
            } finally {
                setSubmitting(false);
            }
        },
    });

    return (
        <Box className="login">
            <Box className="login-image">
                {loading ? <CircularProgress/> :
                    <>
                        <Typography className="school-name">
                            {schoolName}
                        </Typography>
                        <img className="logo" src={schoolLogo} alt="School Logo"/>
                    </>}
            </Box>
            <Box className="login-inputs">
                <Typography className="title">Zaloguj się</Typography>
                <FormikProvider value={formik}>
                    <Form className="form">
                        <EmailInput label="Email" name="email" shouldShrink={true}/>
                        <PasswordInput label="Hasło" name="password" shouldShrink={true}/>
                        <Button className="submit" variant="contained" type="submit" disabled={formik.isSubmitting}>
                            {formik.isSubmitting ? <CircularProgress size={24}/> : "Zaloguj się"}
                        </Button>
                    </Form>
                </FormikProvider>
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
        </Box>
    );
}

export default LogIn;