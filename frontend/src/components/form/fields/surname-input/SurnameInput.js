import React from "react";
import { useField } from "formik";
import * as Yup from "yup";
import "./SurnameInput.scss";
import {TextField} from "@mui/material";

export const SurnameSchema = Yup.string()
    .matches(/^[^\d]*$/, "Nazwisko nie może zawierać cyfr")
    .required("Nazwisko jest wymagane");

const SurnameInput = ({ label, ...props }) => {
    const [field, meta] = useField(props);

    return (
        <TextField
            className="surname-input"
            id={props.name}
            label={label}
            variant="outlined"
            {...field}
            error={meta.touched && Boolean(meta.error)}
            helperText={meta.touched && meta.error}
        />
    );
};

export default SurnameInput;
