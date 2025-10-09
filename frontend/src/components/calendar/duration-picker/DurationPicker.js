import React, { useState } from "react"
import { Box, TextField, Typography } from "@mui/material"
import "./DurationPicker.scss"

const transformStringToCorrectNumber = (stringNumber) => {
    if(stringNumber === ""){
        return "0"
    }
    return stringNumber.replace(/^0+/, "")
}

export default function DurationPicker({ onChange, isActive}) {
    const [duration, setDuration] = useState({ hours: "0", minutes: "0" })

    const handleChange = (e) => {
        const { name, value } = e.target
        if (value === "" || /^\d*$/.test(value)) {
            let newValue = transformStringToCorrectNumber(value)
            if (name === "minutes" && Number(value) > 59) {
                newValue = "59"
            }
            else if(name === "hours" && Number(value) > 10000){
                newValue = "10000"
            }
            const newDuration = { ...duration, [name]: newValue }
            setDuration(newDuration)
            if (onChange) {
                onChange(newDuration.hours * 3600 + newDuration.minutes * 60)
            }
        }
    }

    return (
        <Box className="duration-pickers">
            <TextField
                label="Godziny"
                name="hours"
                type="text"
                value={duration.hours}
                onChange={handleChange}
                inputProps={{ inputMode: "numeric", pattern: "[0-9]*", min: 0, max: 10000 }}
                className="picker"
                size="small"
                disabled={!isActive}
            />
            <Typography>:</Typography>
            <TextField
                label="Minuty"
                name="minutes"
                type="text"
                value={duration.minutes}
                onChange={handleChange}
                inputProps={{ inputMode: "numeric", pattern: "[0-9]*", min: 0, max: 59 }}
                className="picker"
                size="small"
                disabled={!isActive}
            />
        </Box>
    )
}
