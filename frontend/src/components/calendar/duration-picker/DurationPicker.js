import React, {useState} from "react"
import {Box, TextField, Typography} from "@mui/material"
import "./DurationPicker.scss"

const transformStringToCorrectNumber = (stringNumber) => {
    if (stringNumber === "") {
        return "0"
    }
    return stringNumber.replace(/^0+/, "")
}

const createHoursMinutesFromSeconds = (seconds) => {
    const hours = Math.floor(seconds / 3600)
    const remainingSeconds = seconds - hours * 3600
    const minutes = Math.floor( remainingSeconds / 60 )
    return {hours: hours, minutes: minutes}
}

export default function DurationPicker({onChange, isActive, functionDuration}) {
    const [duration, setDuration] = useState(createHoursMinutesFromSeconds(functionDuration))

    const handleChange = (e) => {
        const {name, value} = e.target
        if (value === "" || /^\d*$/.test(value)) {
            let newValue = transformStringToCorrectNumber(value)
            if (name === "minutes" && Number(value) > 59) {
                newValue = "59"
            } else if (name === "hours" && Number(value) > 10000) {
                newValue = "1000"
            }
            const newDuration = {...duration, [name]: newValue}
            setDuration(newDuration)
        }
    }

    const handleBlur = () => {
        onChange(duration.hours * 3600 + duration.minutes * 60)
    }

    return (
        <Box className="duration-pickers">
            <TextField
                label="Godziny"
                name="hours"
                type="text"
                value={duration.hours}
                onChange={handleChange}
                inputProps={{inputMode: "numeric", pattern: "[0-9]*", min: 0, max: 10000}}
                className="picker"
                size="small"
                disabled={!isActive}
                onBlur={handleBlur}
            />
            <Typography>:</Typography>
            <TextField
                label="Minuty"
                name="minutes"
                type="text"
                value={duration.minutes}
                onChange={handleChange}
                inputProps={{inputMode: "numeric", pattern: "[0-9]*", min: 0, max: 59}}
                className="picker"
                size="small"
                disabled={!isActive}
                onBlur={handleBlur}
            />
        </Box>
    )
}
