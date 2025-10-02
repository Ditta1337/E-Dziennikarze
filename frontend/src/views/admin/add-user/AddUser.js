import React, {useEffect, useState} from "react";
import {useFormik, FormikProvider, Form} from "formik";
import * as Yup from "yup";
import {Button, Typography, CircularProgress, Snackbar, Alert} from "@mui/material";
import NameInput, {NameSchema} from "../../../components/form/fields/name-input/NameInput";
import SurnameInput, {SurnameSchema} from "../../../components/form/fields/surname-input/SurnameInput";
import PhoneInput, {PhoneSchema} from "../../../components/form/fields/phone-input/PhoneInput";
import CountryInput, {CountrySchema} from "../../../components/form/fields/country-input/CountryInput";
import AddressCodeInput, {AddressCodeSchema} from "../../../components/form/fields/address-code-input/AddressCodeInput";
import CityInput, {CitySchema} from "../../../components/form/fields/city-input/CityInput";
import AddressInput, {AddressSchema} from "../../../components/form/fields/address-input/AddressInput";
import EmailInput, {EmailSchema} from "../../../components/form/fields/email-input/EmailInput";
import PasswordInput, {PasswordSchema} from "../../../components/form/fields/password-input/PasswordInput";
import SelectInput, {SelectSchema} from "../../../components/form/fields/select-input/SelectInput";
import {GuardianRole, rolesToPolish, StudentRole, TeacherRole} from "../roles";
import {get} from "../../../api";
import {submitUser} from "./submitUser";
import "./AddUser.scss";

const AddUserPage = () => {
    const [guardians, setGuardians] = useState([]);
    const [subjects, setSubjects] = useState([]);

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    useEffect(() => {
        fetchGuardians()
        fetchSubjects()
    }, []);

    const fetchGuardians = async () => {
        try {
            const guardians = await get(`/user/all`, {role: GuardianRole})
            const guardianEmails = guardians.data.map(g => ({
                value: g.id,
                label: `${g.name} ${g.surname} (${g.email})`
            }));
            setGuardians(guardianEmails);
        } catch (error) {
            console.error("Error fetching guardians:", error);
            setSnackbarMessage("Wystąpił błąd podczas pobierania opiekunów");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
        }
    }

    const fetchSubjects = async () => {
        try {
            const subjects = await get(`/subject/all`)
            const subjectOptions = subjects.data.map(s => ({
                value: s.id,
                label: s.name
            }));
            setSubjects(subjectOptions);
        }
        catch (error) {
            console.error("Error fetching subjects:", error);
            setSnackbarMessage("Wystąpił błąd podczas pobierania przedmiotów");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
        }
    }

    const validationSchema = Yup.object({
        name: NameSchema,
        surname: SurnameSchema,
        email: EmailSchema,
        password: PasswordSchema,
        phone: PhoneSchema,
        country: CountrySchema,
        address_code: AddressCodeSchema,
        city: CitySchema,
        address: AddressSchema,
        role: SelectSchema,
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
        initialValues: {
            name: "Artur",
            surname: "Dwornik",
            email: "a@a.a",
            password: "wn5A6LQc3E",
            phone: "+48 12 312 31 23",
            country: "Polska",
            address_code: "31-230",
            city: "Kraków",
            address: "Ulica 32/2",
            role: "",
            guardian_id: "",
            subjects: [],
        },
        validationSchema,
        onSubmit: async (values, {setSubmitting, resetForm}) => {
            try {
                console.log(values);
                await submitUser(values);
                setSnackbarMessage("Użytkownik został dodany pomyślnie");
                setSnackbarSeverity("success");
                setSnackbarOpen(true);
                resetForm();
            } catch (error) {
                console.log(error);
                setSnackbarMessage(error.message);
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
        <div className="add-user">
            <Typography className="title">Dodaj użytkownika</Typography>
            <FormikProvider value={formik}>
                <Form className="form">
                    <NameInput label="Imię" name="name" shouldShrink={true}/>
                    <SurnameInput label="Nazwisko" name="surname" shouldShrink={true}/>
                    <EmailInput label="Email" name="email" shouldShrink={true}/>
                    <PasswordInput label="Hasło" name="password" allowGenerate shouldShrink={true}/>
                    <PhoneInput label="Telefon" name="phone" shouldShrink={true}/>
                    <CountryInput label="Państwo" name="country" shouldShrink={true}/>
                    <AddressCodeInput label="Kod pocztowy" name="address_code" shouldShrink={true}/>
                    <CityInput label="Miasto" name="city" shouldShrink={true}/>
                    <AddressInput label="Adres" name="address" shouldShrink={true}/>
                    <SelectInput label="Rola" name="role" options={rolesToPolish} shouldShrink={true}/>

                    {formik.values.role === StudentRole && (
                        <>
                            <SelectInput
                                label="Opiekun"
                                name="guardian_id"
                                shouldShrink={true}
                                options={guardians}
                            />
                        </>
                    )}

                    {formik.values.role === TeacherRole && (
                        <SelectInput
                            label="Nauczane przedmioty"
                            name="subjects"
                            options={subjects}
                            multi
                            shouldShrink={true}
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
                            "Dodaj użytkownika"
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
        </div>
    );
};

export default AddUserPage;
