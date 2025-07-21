import {TextField} from "@mui/material";
import {useField} from "formik";
import * as Yup from "yup";
import './YearInput.scss'

export const YearSchema = Yup.string()
    .matches(/^\d{4}$/, "Rok musi składać się z czterech cyfr")
    .required("Rok jest wymagany");

const YearInput = ({label, ...props}) => {
    const [field, meta] = useField(props);

    return <>
        <TextField
            className="year-input"
            id={props.name}
            label={label}
            variant="outlined"
            {...field}
            error={meta.touched && Boolean(meta.error)}
            helperText={meta.touched && meta.error}
        />
    </>
}

export default YearInput