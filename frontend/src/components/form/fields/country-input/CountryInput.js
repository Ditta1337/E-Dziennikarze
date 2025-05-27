import React from "react";
import {useField} from "formik";
import * as Yup from "yup";
import {TextField} from "@mui/material";
import "./CountryInput.scss";

export const CountrySchema = Yup.string()
    .matches(/^[^\d]*$/, "Państwo nie może zawierać cyfr")
    .required("Państwo jest wymagane");

const CountryInput = ({label, readOnly = false, shouldShrink = false, ...props}) => {
    const [field, meta] = useField(props);

    return (
        <TextField
            className="country-input"
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

export default CountryInput;