import React from "react";
import { useField } from "formik";
import * as Yup from "yup";
import { Switch } from "@mui/material";
import "./SwitchInput.scss";

export const SwitchSchema = Yup.boolean();

const SwitchInput = ({ name, label, ...props }) => {
    const [field, , helpers] = useField({ name, type: 'checkbox' });
    const { setValue } = helpers;

    return (
        <div className="switch-input">
            <span className="label">{label}</span>
            <Switch
                {...props}
                className="switch"
                checked={field.value || false}
                onChange={(_, checked) => setValue(checked)}
                name={name}
                color="primary"
            />
        </div>
    );
};

export default SwitchInput;