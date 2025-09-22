import React from "react";
import {
    Box,
    Button,
    CircularProgress,
    Typography,
    Checkbox,
    FormControlLabel
} from "@mui/material";
import * as Yup from "yup";
import YearInput, { YearSchema } from "../../components/form/fields/year-input/YearInput";
import { Form, FormikProvider, useFormik } from "formik";
import './CreateGroup.scss';
import GroupCodeInput, { GroupCodeSchema } from "../../components/form/fields/group-code-input/GroupCodeInput";

const CreateGroup = () => {

    const validationSchema = Yup.object({
        startYear: YearSchema,
        groupCode: GroupCodeSchema,
        isClass: Yup.boolean()
    });

    const formik = useFormik({
        initialValues: {
            startYear: "",
            groupCode: "",
            isClass: false
        },
        validationSchema,
        onSubmit: async (values, { setSubmitting, resetForm }) => {
            try {
                resetForm();
            } catch (error) {
                console.error(error);
            } finally {
                setSubmitting(false);
            }
        },
    });

    return (
        <Box className="create-group">
            <Typography className="title">Dodaj grupę</Typography>
            <FormikProvider value={formik}>
                <Form className="form">
                    <YearInput label="Rok Rozpoczęcia" name="startYear" />
                    <GroupCodeInput label="Kod Grupy" name="groupCode" />

                    <FormControlLabel
                        className="class-checkbox"
                        control={
                            <Checkbox
                                name="isClass"
                                checked={formik.values.isClass}
                                onChange={formik.handleChange}
                                sx={{ '& .MuiSvgIcon-root': { fontSize: 30 } }}
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
                            <CircularProgress size={24} />
                        ) : (
                            "Dodaj grupę"
                        )}
                    </Button>
                </Form>
            </FormikProvider>
        </Box>
    )
}

export default CreateGroup;
