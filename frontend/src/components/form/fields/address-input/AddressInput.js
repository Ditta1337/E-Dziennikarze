import React from "react";
import {useField} from "formik";
import * as Yup from "yup";
import {TextField} from "@mui/material";
import "./AddressInput.scss";

export const AddressSchema = Yup.string()
    .required("Adres jest wymagany");

const AddressInput = ({label, readOnly = false, shouldShrink = false, ...props}) => {
    const [field, meta] = useField(props);

    return (
        <TextField
            className="address-input"
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

export default AddressInput;