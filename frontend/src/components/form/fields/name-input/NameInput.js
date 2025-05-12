import React from "react";
import {useField} from "formik";
import * as Yup from "yup";
import {TextField} from "@mui/material";
import "./NameInput.scss"

export const NameSchema = Yup.string()
    .matches(/^[^\d]*$/, "Imie nie może zawierać cyfr")
    .required("Imie jest wymagane");

const NameInput = ({label, ...props}) => {
    const [field, meta] = useField(props);

    return (
        <TextField
            className="input"
            id={props.name}
            label={label}
            variant="outlined"
            {...field}
            error={meta.touched && Boolean(meta.error)}
            helperText={meta.touched && meta.error}
        />
    );
};

export default NameInput;
