import * as Yup from "yup";
import {useField} from "formik";
import {TextField} from "@mui/material";
import "./RoomCodeInput.scss"

export const RoomCodeSchema = Yup.string()
    .max(20, "Kod pokoju nie może być dłuższy niż 20 znaków")
    .required("Pojemność jest wymagana")

const RoomCodeInput = ({label, ...props}) =>{
    const [field, meta] = useField(props);

    return <>
        <TextField
            className="room-code-input"
            id={props.name}
            label={label}
            variant="outlined"
            {...field}
            error={meta.touched && Boolean(meta.error)}
            helperText={meta.touched && meta.error}
        />
    </>
}

export default RoomCodeInput