import "./Profile.scss"
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
import ImageAdder from "../../components/image-adder/ImageAdder";
import PasswordInput, {PasswordSchema} from "../../components/form/fields/password-input/PasswordInput";
import {useStore} from "../../store";
import {get} from "../../api";
import {updateUser} from "../../util/submit/submitUser";
import {prepareUserData} from "../../util/objectUtil";


const Profile = () => {
    const userId = useStore((state) => state.user.userId)
    const [user, setUser] = useState(null);

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");
    const [isEditingPhoto, setIsEditingPhoto] = useState(false);
    const [croppedAvatar, setCroppedAvatar] = useState(null);
    const [isEditingPassword, setIsEditingPassword] = useState(false);

    useEffect(() => {
        fetchUser();
    }, []);

    const fetchUser = async () => {
        try {
            const user = await get(`/user/${userId}`);
            const preparedUser = prepareUserData(user.data)
            setUser(preparedUser);
        } catch (error) {
            console.error("Błąd pobierania danych użytkownika:", error);
            setSnackbarMessage("Wystąpił błąd podczas pobierania danych użytkownika");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
        }
    }

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
        onSubmit: async (values) => {
            try {
                await updateUser({...user, password: values.password}, userId);
                setSnackbarMessage("Hasło zostało zmienione");
                setSnackbarSeverity("success");
                setSnackbarOpen(true);
                setIsEditingPassword(false);
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
        const currentPhoto = user.image_base64;
        try {
            await updateUser({...user, image_base64: croppedAvatar}, userId);
            setUser({...user, image_base64: croppedAvatar})
            setSnackbarMessage("Zdjęcie zaaktualizowane pomyślnie");
            setSnackbarSeverity("success");
            setSnackbarOpen(true);
        } catch (error) {
            console.error(error);
            setUser({...user, image_base64: currentPhoto})
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
                                variant="contained"
                                type="submit"
                                onClick={handlePhotoEdition}
                            >
                                Edytuj Zdjęcie
                            </Button>
                        </>
                    ) : (
                        <>
                            <Box className="image-adder-container">
                                <ImageAdder onImageCropped={handleAvatarCropped}/>
                            </Box>
                            <Button
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
    );
};

export default Profile;