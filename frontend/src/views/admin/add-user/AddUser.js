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
import SwitchInput, {SwitchSchema} from "../../../components/form/fields/switch-input/SwitchInput";
import {rolesToPolish, StudentRole, TeacherRole, WorkerRole} from "../roles";
import {submitUser} from "./submitUser";
import "./AddUser.scss";

const AddUserPage = () => {
    const [guardians, setGuardians] = useState([]);
    const [subjects, setSubjects] = useState([]);

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    useEffect(() => {
        // TODO: get from backend
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
        can_choose_preferences: SwitchSchema,
        principal_privileges: SwitchSchema,
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
            can_choose_preferences: false,
            principal_privileges: false,
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
                console.error(error);
                setSnackbarMessage("Wystąpił błąd podczas dodawania użytkownika");
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
                    <NameInput label="Imię" name="name" />
                    <SurnameInput label="Nazwisko" name="surname" />
                    <EmailInput label="Email" name="email" />
                    <PasswordInput label="Hasło" name="password" allowGenerate />
                    <PhoneInput label="Telefon" name="phone" />
                    <CountryInput label="Państwo" name="country" />
                    <AddressCodeInput label="Kod pocztowy" name="address_code" />
                    <CityInput label="Miasto" name="city" />
                    <AddressInput label="Adres" name="address" />
                    <SelectInput label="Rola" name="role" options={rolesToPolish} />

                    {formik.values.role === StudentRole && (
                        <>
                            <SelectInput
                                label="Opiekun"
                                name="guardian_id"
                                options={guardians}
                            />
                            <SwitchInput
                                label="Sam wybiera preferencje"
                                name="can_choose_preferences"
                            />
                        </>
                    )}

                    {formik.values.role === WorkerRole && (
                        <SwitchInput
                            label="Jest osobą dyrektorską"
                            name="principal_privileges"
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
                            <CircularProgress size={24} />
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
                anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
            >
                <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{ width: '100%' }}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>
        </div>
    );
};

export default AddUserPage;
