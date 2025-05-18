import React from "react";
import {useField} from "formik";
import * as Yup from "yup";
import {TextField} from "@mui/material";
import "./EmailInput.scss";

export const EmailSchema = Yup.string()
    .matches(/^[^\s@]+@[^\s@]+\.[^\s@]+$/, "Podaj poprawny adres email")
    .required("Email jest wymagany");

const EmailInput = ({label, ...props}) => {
    const [field, meta] = useField(props);

    return (
        <TextField
            className="email-input"
            id={props.name}
            label={label}
            variant="outlined"
            {...field}
            error={meta.touched && Boolean(meta.error)}
            helperText={meta.touched && meta.error}
        />
    );
};

export default EmailInput;