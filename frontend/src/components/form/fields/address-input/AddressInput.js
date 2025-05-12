import React from "react";
import { useField } from "formik";
import * as Yup from "yup";
import { TextField } from "@mui/material";
import "./AddressInput.scss";

export const AddressSchema = Yup.string()
    .required("Adres jest wymagany");

const AddressInput = ({ label, ...props }) => {
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

export default AddressInput;