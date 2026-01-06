import { get, put } from "../../../api";
import { useEffect, useState } from "react";
import { Formik, Form } from "formik";
import { Button, Box, Typography, Snackbar, Alert } from "@mui/material";
import PropertyField, { fieldLabels } from "./PropertyField";
import './PropertyEditor.scss';

const PropertyEditor = () => {
    const [properties, setProperties] = useState([]);
    const [isEditing, setIsEditing] = useState(false);
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

    useEffect(() => {
        get("/property/all").then(res => setProperties(res.data));
    }, []);

    const initialValues = properties.reduce((acc, prop) => {
        acc[prop.name] = prop.value;
        return acc;
    }, {});

    const handleCloseSnackbar = () => setSnackbar({ ...snackbar, open: false });

    return (
        <Box className="property-editor">
            <Typography variant="h4">Właściwości szkoły</Typography>

            <Formik
                enableReinitialize
                initialValues={initialValues}
                onSubmit={async (values) => {
                    try {
                        for (const prop of properties) {
                            await put("/property", {
                                id: prop.id,
                                name: prop.name,
                                type: prop.type,
                                defaultValue: prop.defaultValue,
                                value: values[prop.name],
                                save_to_fetch: prop.saveToFetch || false
                            });
                        }

                        setIsEditing(false);
                        const res = await get("/property/all");
                        setProperties(res.data);

                        setSnackbar({ open: true, message: 'Wszystkie wartości zapisane!', severity: 'success' });
                    } catch (error) {
                        console.error(error);
                        setSnackbar({ open: true, message: 'Błąd przy zapisie właściwości', severity: 'error' });
                    }
                }}
            >
                {({ handleSubmit, setFieldValue }) => (
                    <Form>
                        <Box className="property-grid">
                            {properties
                                .filter(prop => prop.name !== "maxLessonsPerDay")
                                .map(prop => (
                                    <Box key={prop.id} className="property-item">
                                        <Box className="field-name">
                                            {fieldLabels[prop.name] || prop.name}
                                        </Box>
                                        <Box className="field-value">
                                            <PropertyField
                                                prop={prop}
                                                isEditing={isEditing}
                                                setFieldValue={setFieldValue}
                                            />
                                        </Box>
                                    </Box>
                            ))}
                        </Box>

                        <Box className="actions">
                            {!isEditing ? (
                                <Button
                                    type="button"
                                    variant="contained"
                                    color="primary"
                                    onClick={() => setIsEditing(true)}
                                >
                                    Edytuj
                                </Button>
                            ) : (
                                <Button
                                    type="button"
                                    variant="contained"
                                    color="success"
                                    onClick={handleSubmit}
                                >
                                    Zapisz
                                </Button>
                            )}
                        </Box>
                    </Form>
                )}
            </Formik>

            <Snackbar open={snackbar.open} autoHideDuration={1000} onClose={handleCloseSnackbar}>
                <Alert onClose={handleCloseSnackbar} severity={snackbar.severity} sx={{ width: '100%' }}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default PropertyEditor;
