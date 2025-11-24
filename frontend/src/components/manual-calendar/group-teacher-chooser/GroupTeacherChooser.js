import {Autocomplete, Box, TextField} from "@mui/material";
import options from "./GroupTeacherOptions"
import {useEffect, useState} from "react";
import "./GroupTeacherChooser.scss"

const GroupTeacherChooser = ({groupData, teacherData, onGroupTeacherOptionChange}) => {
    const [chosenPerspective, setChosenPerspective] = useState(null)
    const [chosenEntity, setChosenEntity] = useState(null)

    const handleChosenPerspectiveChange = (event, value) => {
        setChosenPerspective(value)
        setChosenEntity(null)
        onGroupTeacherOptionChange(null)
    }

    const getSpecificEntityOptions = () => {
        if (chosenPerspective === options.GroupPerspective) {
            return groupData
        } else if (chosenPerspective === options.TeacherPerspective) {
            return teacherData
        }
        return []
    }

    const getSpecificEntityOptionLabel = (option) => {
        if (!option) return ""
        if (chosenPerspective === options.GroupPerspective) {
            return option.group_code || ""
        } else if (chosenPerspective === options.TeacherPerspective) {
            return option.name + " " + option.surname || ""
        }
        return ""
    }

    const setGroupTeacherOption = (option) => {
        onGroupTeacherOptionChange({...option, chosenPerspective})
    }

    return <Box className="group-teacher-chooser">
        <Autocomplete
            className="group-teacher"
            options={Object.values(options)}
            value={chosenPerspective}
            onChange={handleChosenPerspectiveChange}
            renderInput={(params) => (
                <TextField
                    {...params}
                    label="Perspektywa"
                />
            )}
        />
        <Autocomplete
            className="specific-entity"
            options={getSpecificEntityOptions()}
            getOptionLabel={getSpecificEntityOptionLabel}
            value={chosenEntity}
            onChange={(event, value) => {
                setChosenEntity(value);
                setGroupTeacherOption(value);
            }}
            renderInput={(params) => (
                <TextField {...params} label="Wybierz konkretną instancję"/>
            )}
            disabled={!chosenPerspective}
        />

    </Box>
}

export default GroupTeacherChooser