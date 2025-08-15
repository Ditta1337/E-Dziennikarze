import * as Yup from "yup";
import {useField} from "formik";
import {TextField} from "@mui/material";
import "./RoomCapacityInput.scss";

export const RoomCapacitySchema = Yup.string()
    .matches(/^\d+$/, "Pojemność musi być dodatnią liczbą całkowitą")
    .required("Pojemność jest wymagana")

const RoomCapacityInput = ({label, ...props}) => {
    const [field, meta] = useField(props);

    return <>
        <TextField
            className="room-capacity-input"
            id={props.name}
            label={label}
            variant="outlined"
            {...field}
            error={meta.touched && Boolean(meta.error)}
            helperText={meta.touched && meta.error}
        />
    </>
}

export default RoomCapacityInput