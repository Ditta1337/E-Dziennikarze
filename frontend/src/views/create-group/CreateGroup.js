import React, {useState} from "react";
import {
    Box,
    Button,
    CircularProgress,
    Typography,
    Checkbox,
    FormControlLabel,
    Snackbar,
    Alert
} from "@mui/material";
import * as Yup from "yup";
import YearInput, {YearSchema} from "../../components/form/fields/year-input/YearInput";
import {Form, FormikProvider, useFormik} from "formik";
import './CreateGroup.scss';
import GroupCodeInput, {GroupCodeSchema} from "../../components/form/fields/group-code-input/GroupCodeInput";
import {post} from "../../api"

const CreateGroup = () => {
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    const validationSchema = Yup.object({
        startYear: YearSchema,
        groupCode: GroupCodeSchema,
        isClass: Yup.boolean()
    });

    const submitGroup = async (values) => {
        const payload = {
            "start_year": values.startYear,
            "group_code": values.groupCode,
            "class": values.isClass
        }
        console.log(payload)
        await post('/group', payload);
    };

    const formik = useFormik({
        initialValues: {
            startYear: "",
            groupCode: "",
            isClass: false
        },
        validationSchema,
        onSubmit: async (values, {setSubmitting, resetForm}) => {
            try {
                await submitGroup(values);
                setSnackbarMessage("Grupa została dodana pomyślnie");
                setSnackbarSeverity("success");
                setSnackbarOpen(true);
                resetForm();
            } catch (error) {
                console.error(error);
                setSnackbarMessage("Wystąpił błąd podczas dodawania grupy");
                setSnackbarSeverity("error");
                setSnackbarOpen(true);
            } finally {
                setSubmitting(false);
            }
        },
    });

    const handleSnackbarClose = (event, reason) => {
        if (reason === "clickaway") {
            return;
        }
        setSnackbarOpen(false);
    };

    return (
        <Box className="create-group">
            <Typography className="title">Dodaj grupę</Typography>
            <FormikProvider value={formik}>
                <Form className="form">
                    <YearInput label="Rok Rozpoczęcia" name="startYear"/>
                    <GroupCodeInput label="Kod Grupy" name="groupCode"/>

                    <FormControlLabel
                        className="class-checkbox"
                        control={
                            <Checkbox
                                name="isClass"
                                checked={formik.values.isClass}
                                onChange={formik.handleChange}
                                sx={{'& .MuiSvgIcon-root': {fontSize: 30}}}
                            />
                        }
                        label="Grupa jest klasą"
                        labelPlacement="end"
                    />

                    <Button
                        className="submit"
                        variant="contained"
                        type="submit"
                        disabled={formik.isSubmitting}
                    >
                        {formik.isSubmitting ? (
                            <CircularProgress size={24}/>
                        ) : (
                            "Dodaj grupę"
                        )}
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
    )
}

export default CreateGroup;