import React, {useState} from "react";
import {Form, FormikProvider, useFormik} from "formik";
import {Box, Button, CircularProgress, Typography} from "@mui/material";
import RoomCapacityInput, {
    RoomCapacitySchema
} from "../../components/form/fields/room-capacity-input/RoomCapacityInput";
import * as Yup from "yup";
import RoomCodeInput, {RoomCodeSchema} from "../../components/form/fields/RoomCodeInput/RoomCodeInput";
import "./CreateRoom.scss"

const CreateRoom = () => {
    const [loading, setLoading] = useState(false)

    const validationSchema = Yup.object({
        room_code: RoomCodeSchema,
        capacity: RoomCapacitySchema
    })

    const formik = useFormik({
        initialValues: {
            room_code: "",
            capacity: "",
        },
        validationSchema,
        onSubmit: async (values, {resetForm}) => {
            try {
                setLoading(true);
                await new Promise((res) => setTimeout(res, 500));
                resetForm();
            } catch (error) {
                console.error(error)
            } finally {
                setLoading(false)
            }
        }
    })

    return <Box className="create-room">
        <Typography>Dodaj pokój</Typography>
        <FormikProvider value={formik}>
            <Form className="form">
                <RoomCodeInput label="Kod pokoju" name="room_code"/>
                <RoomCapacityInput label="Pojemność" name="capacity"/>
                <Button
                    className="submit"
                    variant="contained"
                    type="submit"
                    disabled={formik.isSubmitting}
                >
                    {formik.isSubmitting ? (
                        <CircularProgress size={24} />
                    ) : (
                        "Dodaj pokój"
                    )}
                </Button>
            </Form>
        </FormikProvider>
    </Box>
}

export default CreateRoom