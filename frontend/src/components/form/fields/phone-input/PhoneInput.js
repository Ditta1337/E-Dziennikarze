import React from "react";
import {useField} from "formik";
import {MuiTelInput} from "mui-tel-input";
import * as Yup from "yup";
import "./PhoneInput.scss";

export const PhoneSchema = Yup.string()
    .transform((val) => (val ? val.replace(/\D/g, "") : ""))
    .matches(/^\d+$/, "Telefon nie może zawierać liter")
    .required("Telefon jest wymagany");

const PhoneInput = ({label, readOnly = false, shouldShrink = false, ...props}) => {
    const [field, meta, helpers] = useField(props);

    const handleChange = (newValue) => {
        helpers.setValue(newValue);
    };

    return (
        <div className="phone-input">
            <MuiTelInput
                className="input"
                id={props.name}
                disabled={readOnly}
                InputLabelProps={{
                    shrink: shouldShrink,
                }}
                name={props.name}
                label={label}
                value={field.value}
                defaultCountry="PL"
                onChange={handleChange}
                onBlur={field.onBlur}
                error={meta.touched && Boolean(meta.error)}
                helperText={meta.touched && meta.error}
            />
        </div>
    );
};

export default PhoneInput;
