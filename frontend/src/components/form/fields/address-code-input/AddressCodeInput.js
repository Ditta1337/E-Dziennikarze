import React from "react";
import { useField } from "formik";
import * as Yup from "yup";
import { TextField } from "@mui/material";
import "./AddressCodeInput.scss";

export const AddressCodeSchema = Yup.string()
    .matches(/^\d{2}-\d{3}$/, "Kod pocztowy musi mieć format XX-XXX")
    .required("Kod pocztowy jest wymagany");

const AddressCodeInput = ({ label, ...props }) => {
    const [field, meta] = useField(props);

    return (
        <TextField
            className="address-code-input"
            id={props.name}
            label={label}
            variant="outlined"
            {...field}
            error={meta.touched && Boolean(meta.error)}
            helperText={meta.touched && meta.error}
        />
    );
};

export default AddressCodeInput;