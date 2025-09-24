import {useParams} from "react-router";
import React, {useEffect, useState} from "react";
import * as Yup from "yup";
import NameInput, {NameSchema} from "../../../components/form/fields/name-input/NameInput";
import SurnameInput, {SurnameSchema} from "../../../components/form/fields/surname-input/SurnameInput";
import EmailInput, {EmailSchema} from "../../../components/form/fields/email-input/EmailInput";
import PhoneInput, {PhoneSchema} from "../../../components/form/fields/phone-input/PhoneInput";
import CountryInput, {CountrySchema} from "../../../components/form/fields/country-input/CountryInput";
import AddressCodeInput, {AddressCodeSchema} from "../../../components/form/fields/address-code-input/AddressCodeInput";
import CityInput, {CitySchema} from "../../../components/form/fields/city-input/CityInput";
import AddressInput, {AddressSchema} from "../../../components/form/fields/address-input/AddressInput";
import SelectInput from "../../../components/form/fields/select-input/SelectInput";
import {rolesToPolish, StudentRole, TeacherRole} from "../roles";
import {Alert, Avatar, Box, Button, CircularProgress, Snackbar, Typography} from "@mui/material";
import {Form, FormikProvider, useFormik} from "formik";
import {submitUser} from "../add-user/submitUser";
import "./EditUser.scss"

function EditUser() {
    const {id} = useParams();
    const [user, setUser] = useState(null);
    const [guardians, setGuardians] = useState();
    const [subjects, setSubjects] = useState();

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    useEffect(() => { //TODO connect to back, get information based on the userType
        setUser(
            {
                name: "Artur",
                surname: "Dwornik",
                email: "a@a.a",
                phone: "+48 12 312 31 23",
                country: "Polska",
                address_code: "31-230",
                city: "Kraków",
                address: "Ulica 32/2",
                role: StudentRole,
                image_base64: 'https://bi.im-g.pl/im/0a/27/16/z23231498Q,Joanna-Senyszyn.jpg',
            }
        );
        setGuardians([
            {label: "guardian1@gmail.com", value: "guardian1uuid"},
            {label: "guardian2@gmail.com", value: "guardian2uuid"},
            {label: "guardian3@gmail.com", value: "guardian3uuid"},
        ]);
        setSubjects([
            {label: "Matematyka", value: "matematykauuid"},
            {label: "J. Polski", value: "polskiuuid"},
            {label: "Informatyka", value: "informatykauuid"},
            {label: "Fizyka", value: "fizykauuid"},
        ]);
    }, []);

    const validationSchema = Yup.object({
        name: NameSchema,
        surname: SurnameSchema,
        email: EmailSchema,
        phone: PhoneSchema,
        country: CountrySchema,
        address_code: AddressCodeSchema,
        city: CitySchema,
        address: AddressSchema,
        guardian_id: Yup.string()
            .when("role", (role, schema) =>
                role === StudentRole
                    ? schema.required("Wybór opiekuna jest wymagany")
                    : schema.notRequired()
            ),
        subjects: Yup.array().when("role", (role, schema) =>
            role === TeacherRole
                ? schema.min(1, "Wybór conajmniej jednego przedmiotu jest wymagany")
                : schema.notRequired()
        ),
    });

    const formik = useFormik({
        enableReinitialize: true,
        initialValues: {
            ...(user || {}),
        },
        validationSchema,
        onSubmit: async (values, {setSubmitting, resetForm}) => {
            try {
                console.log(values);
                await submitUser(values);
                setSnackbarMessage("Dane użytkownika zostały zaktualizowane pomyślnie");
                setSnackbarSeverity("success");
                setSnackbarOpen(true);
                resetForm();
            } catch (error) {
                console.error(error);
                setSnackbarMessage("Wystąpił błąd podczas aktualizacji danych użytkownika");
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

    const handlePhotoDeletion = () => {
        if (user) {
            setUser({...user, image_base64: null})
        }
    }

    return (
        <Box className="edit-user">
            <Typography className="title">Edytuj użytkownika</Typography>
            <Box className="user-data">
                <Box className="edit-avatar">
                    {user && user.image_base64 ? (
                        <Avatar className="avatar" alt={user.name} src={user.image_base64}/>
                    ) : (
                        <Avatar className="avatar"/>
                    )}

                    <Button
                        className="delete-avatar"
                        variant="contained"
                        type="submit"
                        onClick={handlePhotoDeletion}
                    >
                        Usuń zdjęcie użytkownika
                    </Button>
                </Box>
                <Box className="edit-user-form">
                    <FormikProvider value={formik}>
                        <Form className="form">
                            <NameInput label="Imię" name="name" shouldShrink={true}/>
                            <SurnameInput label="Nazwisko" name="surname" shouldShrink={true}/>
                            <EmailInput label="Email" name="email" shouldShrink={true}/>
                            <PhoneInput label="Telefon" name="phone" shouldShrink={true}/>
                            <CountryInput label="Państwo" name="country" shouldShrink={true}/>
                            <AddressCodeInput label="Kod pocztowy" name="address_code" shouldShrink={true}/>
                            <CityInput label="Miasto" name="city" shouldShrink={true}/>
                            <AddressInput label="Adres" name="address" shouldShrink={true}/>
                            <SelectInput label="Rola" name="role" options={rolesToPolish} readOnly={true}
                                         shouldShrink={true}/>

                            {formik.values.role === StudentRole && (
                                <SelectInput
                                    label="Opiekun"
                                    name="guardian_id"
                                    options={guardians}
                                />
                            )}

                            {formik.values.role === TeacherRole && (
                                <SelectInput
                                    label="Nauczane przedmioty"
                                    name="subjects"
                                    options={subjects}
                                    multi
                                />
                            )}

                            <Button
                                className="submit"
                                variant="contained"
                                type="submit"
                                disabled={formik.isSubmitting}
                            >
                                {formik.isSubmitting ? (
                                    <CircularProgress size={24}/>
                                ) : (
                                    "Aktualizuj dane użytkownika"
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

            </Box>
        </Box>


    );
}

export default EditUser;