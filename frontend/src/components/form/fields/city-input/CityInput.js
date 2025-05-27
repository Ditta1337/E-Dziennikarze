import React from "react";
import {useField} from "formik";
import * as Yup from "yup";
import {TextField} from "@mui/material";
import "./CityInput.scss";

export const CitySchema = Yup.string()
    .matches(/^[^\d]*$/, "Miasto nie może zawierac cyfr")
    .required("Miasto jest wymagane");

const CityInput = ({label, readOnly = false, shouldShrink = false, ...props}) => {
    const [field, meta] = useField(props);

    return (
        <TextField
            className="city-input"
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

export default CityInput;