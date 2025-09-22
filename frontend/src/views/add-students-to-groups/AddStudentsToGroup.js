import React, { useState } from "react";
import {
    Box,
    Button,
    CircularProgress,
    Typography,
    Autocomplete,
    TextField
} from "@mui/material";
import { useFormik } from "formik";
import './AddStudentsToGroup.scss';

const dummyGroups = [
    { id: 1, group_code: "A", start_year: "2023", is_class: true },
    { id: 2, group_code: "Niemiecki grupa 5", start_year: "2022", is_class: false },
    { id: 3, group_code: "C", start_year: "2021", is_class: true },
];

const dummyStudents = [
    {
        id: 101,
        name: "Jan",
        surname: "Kowalski",
        created_at: "2023-09-15",
        address: "ul. Długa 12, Warszawa",
        email: "jan.kowalski@example.com",
        contact: "+48 600 100 101",
        image_url: "https://stoppasozytom.pl/img/asset/bWFpbi9hcnR5a3VseS9waWVyd3N6eS1waWVzL3BpZXJ3c3p5LXBpZXMtby1jenltLXBhbWlldGFjLXNtYWxsLmpwZw==?w=760&h=427&fit=crop&s=3d6245a5f13aadd7c4edb31679df49bd"
    },
    {
        id: 102,
        name: "Anna",
        surname: "Nowak",
        created_at: "2022-06-05",
        address: "ul. Słoneczna 5, Kraków",
        email: "anna.nowak@example.com",
        contact: "+48 601 200 202",
        image_url: "https://sklepzkarmami.pl/poradnik/wp-content/uploads/2022/08/aidi-scaled.jpg"
    },
    {
        id: 103,
        name: "Piotr",
        surname: "Zieliński",
        created_at: "2024-01-10",
        address: "ul. Leśna 44, Gdańsk",
        email: "piotr.z@example.com",
        contact: "+48 602 300 303",
        image_url: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcThmIFs-N4PT0D3gxuDINqgkKWhSxR6sNwJ6g&s"
    }
];



const AddStudentsToGroup = () => {
    const [loading, setLoading] = useState(false);

    const formik = useFormik({
        initialValues: {
            group: null,
            students: [],
        },
        onSubmit: async (values, { resetForm }) => {
            try {
                setLoading(true);
                await new Promise((res) => setTimeout(res, 500));
                resetForm({
                    values: {
                        group: null,
                        students: [],
                    }
                });
            } catch (error) {
                console.error(error);
            } finally {
                setLoading(false);
            }
        }
    });

    return (
        <Box className="add-students-view">
            <Typography className="title">Dodaj uczniów do grupy</Typography>

            <form onSubmit={formik.handleSubmit} className="form">
                <Autocomplete
                    options={dummyGroups}
                    getOptionLabel={(option) => option.group_code + " - " + option.start_year}
                    onChange={(_, value) => formik.setFieldValue("group", value)}
                    renderInput={(params) => (
                        <TextField {...params} label="Wybierz grupę" variant="outlined" />
                    )}
                    className="input"
                />

                <Autocomplete
                    multiple
                    options={dummyStudents}
                    getOptionLabel={(option) => option.name}
                    onChange={(_, value) => formik.setFieldValue("students", value)}
                    renderOption={(props, option) => (
                        <Box component="li" {...props} sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <img
                                src={option.image_url}
                                alt={option.name}
                                width={32}
                                height={32}
                                style={{ borderRadius: '50%' }}
                            />
                            <Box>
                                <Typography variant="body1">{option.name} {option.surname}</Typography>
                                <Typography variant="body2" color="text.secondary">{option.email}</Typography>
                            </Box>
                        </Box>
                    )}
                    renderInput={(params) => (
                        <TextField {...params} label="Wybierz uczniów" variant="outlined" />
                    )}
                    className="input"
                />

                <Button
                    className="submit"
                    variant="contained"
                    type="submit"
                    disabled={loading || !formik.values.group || formik.values.students.length === 0}
                >
                    {loading ? <CircularProgress size={24} /> : "Dodaj uczniów"}
                </Button>
            </form>
        </Box>
    );
};

export default AddStudentsToGroup;
