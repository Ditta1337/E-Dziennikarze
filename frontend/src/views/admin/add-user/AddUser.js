import React from "react";
import {useFormik, FormikProvider, Form} from "formik";
import * as Yup from "yup";
import {Button, Typography} from "@mui/material";
import NameInput, {NameSchema} from "../../../components/form/fields/name-input/NameInput";
import SurnameInput, {SurnameSchema} from "../../../components/form/fields/surname-input/SurnameInput";
import PhoneInput, {PhoneSchema} from "../../../components/form/fields/phone-input/PhoneInput";
import CountryInput, {CitySchema} from "../../../components/form/fields/city-input/CityInput";
import AddressCodeInput, {AddressCodeSchema} from "../../../components/form/fields/address-code-input/AddressCodeInput";
import CityInput, {CountrySchema} from "../../../components/form/fields/country-input/CountryInput";
import AddressInput, {AddressSchema} from "../../../components/form/fields/address-input/AddressInput";
import EmailInput, {EmailSchema} from "../../../components/form/fields/email-input/EmailInput";
import PasswordInput, {PasswordSchema} from "../../../components/form/fields/password-input/PasswordInput";
import "./AddUser.scss";

const AddUser = () => {
    const validationSchema = Yup.object({
        name: NameSchema,
        surname: SurnameSchema,
        email: EmailSchema,
        password: PasswordSchema,
        phone: PhoneSchema,
        country: CountrySchema,
        address_code: AddressCodeSchema,
        city: CitySchema,
        address: AddressSchema
    });

    const formik = useFormik({
        initialValues: {name: "", surname: "", email: "", password: "", phone: "", country: "", address_code: "", city: "", address: ""},
        validationSchema,
        onSubmit: (values) => {
            console.log("form:", values);
        },
    });

    return (
        <div className="add-user">
            <Typography className="title">Add user</Typography>
            <FormikProvider value={formik}>
                <Form className="form">
                    <NameInput label="Imię" name="name"/>
                    <SurnameInput label="Nazwisko" name="surname"/>
                    <EmailInput label="Email" name="email"/>
                    <PasswordInput label="Hasło" name="password" allowGenerate={true}/>
                    <PhoneInput label="Telefon" name="phone"/>
                    <CountryInput label="Państwo" name="country"/>
                    <AddressCodeInput label="Kod pocztowy" name="address_code"/>
                    <CityInput label="Miasto" name="city"/>
                    <AddressInput label="Adres" name="address"/>
                    <Button className="submit" variant="contained" type="submit">Add user</Button>
                </Form>
            </FormikProvider>
        </div>
    );
};

export default AddUser;
