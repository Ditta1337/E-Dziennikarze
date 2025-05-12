import React, { useState } from "react";
import { useField, useFormikContext } from "formik";
import * as Yup from "yup";
import { TextField, IconButton, InputAdornment } from "@mui/material";
import { Visibility, VisibilityOff, Lock } from "@mui/icons-material";
import password from "secure-random-password";
import "./PasswordInput.scss";

export const PasswordSchema = Yup.string()
    .min(2, "Hasło musi mieć przynajmniej 2 znaki")
    .matches(/[A-Za-z]/, "Hasło musi zawierać przynajmniej jedną literę")
    .matches(/\d/, "Hasło musi zawierać przynajmniej cyfrę")
    .required("Hasło jest wymagane");

const PasswordInput = ({ label, allowGenerate = false, ...props }) => {
    const [field, meta] = useField(props);
    const { setFieldValue } = useFormikContext();
    const [showPassword, setShowPassword] = useState(false);

    const handleClickShowPassword = () => {
        setShowPassword((prev) => !prev);
    };

    const handleGeneratePassword = () => {
        const pwd = password.randomPassword({
            length: 10,
            characters: [
                password.lower,
                password.upper,
                password.digits,
            ],
        });
        setFieldValue(field.name, pwd);
    };

    return (
        <TextField
            className="input"
            id={field.name}
            label={label}
            variant="outlined"
            type={showPassword ? "text" : "password"}
            {...field}
            error={meta.touched && Boolean(meta.error)}
            helperText={meta.touched && meta.error}
            InputProps={{
                endAdornment: (
                    <InputAdornment position="end">
                        <IconButton
                            aria-label={showPassword ? "Ukryj hasło" : "Pokaż hasło"}
                            onClick={handleClickShowPassword}
                            edge="end"
                        >
                            {showPassword ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                        {allowGenerate && (
                            <IconButton
                                aria-label="Generuj hasło"
                                onClick={handleGeneratePassword}
                                edge="end"
                            >
                                <Lock />
                            </IconButton>
                        )}
                    </InputAdornment>
                ),
            }}
        />
    );
};

export default PasswordInput;
