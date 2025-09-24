import "./Profile.scss"
import {StudentRole, TeacherRole} from "../admin/roles";
import React, {useEffect, useState} from "react";
import * as Yup from "yup";
import NameInput from "../../components/form/fields/name-input/NameInput";
import SurnameInput from "../../components/form/fields/surname-input/SurnameInput";
import EmailInput from "../../components/form/fields/email-input/EmailInput";
import PhoneInput from "../../components/form/fields/phone-input/PhoneInput";
import CountryInput from "../../components/form/fields/country-input/CountryInput";
import AddressCodeInput from "../../components/form/fields/address-code-input/AddressCodeInput";
import CityInput from "../../components/form/fields/city-input/CityInput";
import AddressInput from "../../components/form/fields/address-input/AddressInput";
import {Form, FormikProvider, useFormik} from "formik";
import {Alert, Avatar, Box, Button, Snackbar, Typography} from "@mui/material";
import SelectInput from "../../components/form/fields/select-input/SelectInput";
import ImageAdder from "../../components/image-adder/ImageAdder";
import {submitPassword, submitPhoto} from "../../components/image-adder/submit";
import PasswordInput, {PasswordSchema} from "../../components/form/fields/password-input/PasswordInput";

const Profile = () => {
    const id = 0; //TODO get it from store when authorization is done
    const [user, setUser] = useState(null);
    const [guardians, setGuardians] = useState();
    const [subjects, setSubjects] = useState();

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");
    const [isEditingPhoto, setIsEditingPhoto] = useState(false);
    const [croppedAvatar, setCroppedAvatar] = useState(null);
    const [isEditingPassword, setIsEditingPassword] = useState(false);

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
                role: TeacherRole,
                image_base64: 'https://bi.im-g.pl/im/0a/27/16/z23231498Q,Joanna-Senyszyn.jpg',
                guardian_id: 'guardian2uuid',
                can_choose_preferences: true,
                principal_privileges: true,
                subjects: ["j.Polski, Matematyka, Angielski"]
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

    const formik = useFormik({
        enableReinitialize: true,
        initialValues: {
            ...(user || {}),
        },
    });

    const passwordFormik = useFormik({
        initialValues: {
            password: "",
        },
        validationSchema: Yup.object({
            password: PasswordSchema
        }),
        onSubmit: async (values, { resetForm }) => {
            try {
                await submitPassword(values.password, id);
                setSnackbarMessage("Hasło zostało zmienione");
                setSnackbarSeverity("success");
                setSnackbarOpen(true);
                setIsEditingPassword(false);
                resetForm();
            } catch (error) {
                console.error("Błąd zmiany hasła:", error);
                setSnackbarMessage("Wystąpił błąd podczas zmiany hasła");
                setSnackbarSeverity("error");
                setSnackbarOpen(true);
            }
        },
    });


    const handleSnackbarClose = (event, reason) => {
        if (reason === "clickaway") {
            return;
        }
        setSnackbarOpen(false);
    };

    const handlePhotoEdition = () => {
        setIsEditingPhoto(true);
    }

    const handleAvatarCropped = (imageUrl) => {
        setCroppedAvatar(imageUrl);
    }

    const handleSaveAvatar = async () => {
        setIsEditingPhoto(false);
        try {
            setUser({...user, image_base64: croppedAvatar})
            await submitPhoto(croppedAvatar, id);
            setSnackbarMessage("Zdjęcie zaaktualizowane pomyślnie");
            setSnackbarSeverity("success");
            setSnackbarOpen(true);
        } catch (error) { //TODO make the old photo display when patching is unsuccesfull, right now it's like this for presentation purpouses
            console.error(error);
            setSnackbarMessage("Wystąpił błąd podczas aktualizacji zdjęcia");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
        }
    }

    const handlePasswordChange = () => {
        setIsEditingPassword(true);
    }

    return (
        <Box className="user-profile">
            <Typography className="title">Twój Profil</Typography>
            <Box className="user-data">
                <Box className="edit-avatar">
                    {!isEditingPhoto ? (
                        <>
                            {user && user.image_base64 ? (
                                <Avatar className="avatar" alt={user.name} src={user.image_base64}/>
                            ) : (
                                <Avatar className="avatar"/>
                            )}
                            <Button
                                className="edit-avatar"
                                variant="contained"
                                type="submit"
                                onClick={handlePhotoEdition}
                            >
                                Edytuj Zdjęcie
                            </Button>
                        </>
                    ) : (
                        <>
                            <ImageAdder onImageCropped={handleAvatarCropped}/>
                            <Button
                                className="save-avatar"
                                variant="contained"
                                type="submit"
                                onClick={handleSaveAvatar}>
                                Zapisz Zdjęcie
                            </Button>
                        </>
                    )}

                </Box>
                <Box className="edit-user-form">
                    <FormikProvider value={formik}>
                        <Form className="form">
                            <NameInput label="Imię" name="name" readOnly={true} shouldShrink={true}/>
                            <SurnameInput label="Nazwisko" name="surname" readOnly={true} shouldShrink={true}/>
                            <EmailInput label="Email" name="email" readOnly={true} shouldShrink={true}/>
                            <PhoneInput label="Telefon" name="phone" readOnly={true} shouldShrink={true}/>
                            <CountryInput label="Państwo" name="country" readOnly={true} shouldShrink={true}/>
                            <AddressCodeInput label="Kod pocztowy" name="address_code" readOnly={true}
                                              shouldShrink={true}/>
                            <CityInput label="Miasto" name="city" readOnly={true} shouldShrink={true}/>
                            <AddressInput label="Adres" name="address" readOnly={true} shouldShrink={true}/>

                            {formik.values.role === StudentRole && (
                                <>
                                    <SelectInput
                                        label="Opiekun"
                                        name="guardian_id"
                                        options={guardians}
                                        readOnly={true}
                                        shouldShrink={true}
                                    />
                                    <Box className="yes-no-answer">
                                        <Typography className="statement">Sam wybiera preferencje:</Typography>
                                        {user.can_choose_preferences ? (
                                            <Typography className="answer">Tak</Typography>
                                        ) : (
                                            <Typography className="answer">Nie</Typography>
                                        )}
                                    </Box>
                                </>
                            )}

                            {formik.values.role === TeacherRole && ( //TODO fix displaying subjects
                                <SelectInput
                                    readOnly={true}
                                    shouldShrink={true}
                                    label="Nauczane przedmioty"
                                    name="subjects"
                                    options={subjects}
                                    multi
                                />
                            )}
                        </Form>
                        {!isEditingPassword ? (
                            <Button className="change-password"
                                    variant="contained"
                                    onClick={handlePasswordChange}>
                                Zmień hasło
                            </Button>
                        ) : (
                            <FormikProvider value={passwordFormik}>
                                <Form className="password-form" onSubmit={passwordFormik.handleSubmit}>
                                    <PasswordInput label="Hasło" name="password"/>
                                    <Button className="change-password"
                                            variant="contained"
                                            type="submit">
                                        Zapisz hasło
                                    </Button>
                                </Form>
                            </FormikProvider>
                        )}
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
    )
        ;
};

export default Profile;