import React from "react";
import {useField} from "formik";
import * as Yup from "yup";
import {TextField} from "@mui/material";
import "./NameInput.scss"

export const NameSchema = Yup.string()
    .matches(/^[^\d]*$/, "Imie nie może zawierać cyfr")
    .required("Imie jest wymagane");

const NameInput = ({label, readOnly = false, shouldShrink = false, ...props}) => {
    const [field, meta] = useField(props);

    return (
        <TextField
            className="name-input"
            id={props.name}
            disabled={readOnly}
            label={label}
            variant="outlined"
            InputLabelProps={{
                shrink: shouldShrink,
            }}
            {...field}
            error={meta.touched && Boolean(meta.error)}
            helperText={meta.touched && meta.error}
        />
    );
};

export default NameInput;
