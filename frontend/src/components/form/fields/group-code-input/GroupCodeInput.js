import * as Yup from "yup";
import {useField} from "formik";
import {TextField} from "@mui/material";
import './GroupCodeInput.scss'

export const GroupCodeSchema = Yup.string()
    .matches(
        /^\d+_.+$/,
        "Kod musi zaczynać się od 'numer_' (np. 12_dowolny_tekst)"
    )
    .required("Kod grupy jest wymagany")

const GroupCodeInput = ({label, ...props}) => {
    const [field, meta] = useField(props);

    return <>
        <TextField
            className="group-code-input"
            id={props.name}
            label={label}
            variant="outlined"
            {...field}
            error={meta.touched && Boolean(meta.error)}
            helperText={meta.touched && meta.error}
        />
    </>
}

export default GroupCodeInput