import {FormControl, InputLabel, MenuItem, Select} from "@mui/material";
import React from "react";
import "./GuardianStudentChooser.scss"

const GuardiansStudentChooser = ({selectedStudentId, handleStudentChange, students}) => {
    return <FormControl className="user-input">
        <InputLabel id="student-select-label">Wybierz ucznia</InputLabel>
        <Select
            labelId="student-select-label"
            value={selectedStudentId}
            label="Wybierz ucznia"
            onChange={handleStudentChange}
        >
            {students.map((student) => (
                <MenuItem key={student.id} value={student.id}>
                    {`${student.name} ${student.surname}`}
                </MenuItem>
            ))}
        </Select>
    </FormControl>
}

export default GuardiansStudentChooser