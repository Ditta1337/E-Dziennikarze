import React from "react";
import { useField, useFormikContext } from "formik";
import * as Yup from "yup";
import { Autocomplete, TextField } from "@mui/material";
import "./SelectInput.scss";

export const SelectSchema = Yup.string()
    .required("Wybór jest wymagany");

const SelectInput = ({ name, label, options, multi = false, readOnly = false, shouldShrink = false, ...props }) => {
    const [field, meta] = useField(name);
    const { setFieldValue } = useFormikContext();

    const selectedOption = multi
        ? options.filter(opt => Array.isArray(field.value) && field.value.includes(opt.value))
        : options.find(opt => opt.value === field.value) || null;

    return (
        <Autocomplete
            className="select-input"
            {...props}
            multiple={multi}
            options={options}
            getOptionLabel={(option) => option.label}
            isOptionEqualToValue={(option, value) => option.value === value.value}
            value={selectedOption}
            onChange={(_, option) => {
                if (multi) {
                    const values = Array.isArray(option)
                        ? option.map(opt => opt.value)
                        : [];
                    setFieldValue(name, values);
                } else {
                    setFieldValue(name, option ? option.value : "");
                }
            }}
            openOnFocus
            clearOnEscape
            clearOnBlur
            disabled ={readOnly}
            renderInput={(params) => (
                <TextField
                    {...params}
                    name={name}
                    label={label}
                    variant="outlined"
                    InputLabelProps={{
                        shrink: shouldShrink,
                    }}
                    error={Boolean(meta.touched && meta.error)}
                    helperText={meta.touched && meta.error}
                />
            )}
        />
    );
};

export default SelectInput;
