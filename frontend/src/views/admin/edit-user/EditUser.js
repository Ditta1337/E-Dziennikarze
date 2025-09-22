import React, {useEffect, useState} from "react";
import {useParams} from "react-router";
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
import {GuardianRole, rolesToPolish, StudentRole, TeacherRole} from "../roles";
import {Alert, Avatar, Box, Button, CircularProgress, Snackbar, Typography} from "@mui/material";
import {Form, FormikProvider, useFormik} from "formik";
import {updateUser} from "../../../util/submit/submitUser";
import "./EditUser.scss";
import {get} from "../../../api";
import {prepareUserData} from "../../../util/objectUtil";

function EditUser() {
    const {id} = useParams();
    const [user, setUser] = useState(null);
    const [guardians, setGuardians] = useState([]);
    const [currentGuardiansIds, setCurrentGuardiansIds] = useState([]);
    const [subjects, setSubjects] = useState([]);
    const [currentSubjectsIds, setCurrentSubjectsIds] = useState([]);
    const [loading, setLoading] = useState(true);

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    useEffect(() => {
        fetchData();
    }, [id]);

    const fetchData = async () => {
        try {
            const [userData, guardiansData, subjectsData, currentGuardiansData, currentSubjectsData] = await Promise.all([
                get(`/user/${id}`),
                get(`/user/all`, {role: GuardianRole}),
                get(`/subject/all`),
                get(`/student-guardian/student/${id}`),
                get(`/subject-taught/teacher/${id}`)
            ]);


            const preparedUser = prepareUserData(userData.data)
            const allGuardians = guardiansData.data.map(g => ({
                value: g.id,
                label: `${g.name} ${g.surname} (${g.email})`
            }));

            const currentGuardianIds = currentGuardiansData.data.map(g => g.id);

            const allSubjects = subjectsData.data.map(s => ({
                value: s.id,
                label: s.name
            }));

            const currentSubjectIds = currentSubjectsData.data.map(s => s.id);

            setGuardians(allGuardians);
            setSubjects(allSubjects);
            setCurrentSubjectsIds(currentSubjectIds);
            setCurrentGuardiansIds(currentGuardianIds);

            setUser({
                ...preparedUser,
                guardian_ids: currentGuardianIds,
                subjects: currentSubjectIds,
            });

        } catch (error) {
            console.error("Error fetching data:", error);
            setSnackbarMessage("Wystąpił błąd podczas pobierania danych");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
        } finally {
            setLoading(false);
        }
    };

    const validationSchema = Yup.object({
        name: NameSchema,
        surname: SurnameSchema,
        email: EmailSchema,
        phone: PhoneSchema,
        country: CountrySchema,
        address_code: AddressCodeSchema,
        city: CitySchema,
        address: AddressSchema,
        guardian_ids: Yup.array().when("role", (role, schema) =>
            role === StudentRole
                ? schema.min(1, "Wybór opiekuna jest wymagany")
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
        onSubmit: async (values, {setSubmitting}) => {
            try {
                await updateUser(values, id, currentGuardiansIds, currentSubjectsIds);
                setSnackbarMessage("Dane użytkownika zostały zaktualizowane pomyślnie");
                setSnackbarSeverity("success");
                setSnackbarOpen(true);
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

    if (loading) {
        return <CircularProgress/>;
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
                        variant="contained"
                        type="submit"
                        onClick={handlePhotoDeletion}
                    >
                        Usuń zdjęcie
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
                                    name="guardian_ids"
                                    shouldShrink={true}
                                    options={guardians}
                                    multi
                                />
                            )}

                            {formik.values.role === TeacherRole && (
                                <SelectInput
                                    label="Nauczane przedmioty"
                                    name="subjects"
                                    options={subjects}
                                    shouldShrink={true}
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