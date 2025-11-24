import {useEffect, useState} from "react";
import {Autocomplete, Box, TextField} from "@mui/material";
import options from "../group-teacher-chooser/GroupTeacherOptions"
import "./LessonConstructor.scss"

const LessonConstructor = ({groupTeacherOption, roomData, teacherData, groupData}) => {
    const [chosenRoom, setChosenRoom] = useState(null)
    const [dynamicGroupTeacherOption, setDynamicGroupTeacherOption] = useState(null)

    const handleRoomChange = (event, room) => {
        setChosenRoom(room);
    };

    const handleDynamicGroupTeacherOption = (event, option) => {
        setDynamicGroupTeacherOption(option)
    }

    const getDynamicGroupTeacherOptions = () => {
        if(!groupTeacherOption){
            return []
        }
        if(groupTeacherOption.chosenPerspective === options.GroupPerspective){
            return teacherData
        }
        if(groupTeacherOption.chosenPerspective === options.TeacherPerspective){
            return groupData
        }
        return []
    }

    const getDynamicGroupTeacherOptionLabels = (option) => {
        if(!groupTeacherOption){
            return []
        }
        if(groupTeacherOption.chosenPerspective === options.GroupPerspective){
            return option.name + " " + option.surname
        }
        if(groupTeacherOption.chosenPerspective === options.TeacherPerspective){
            return option.group_code
        }
        return []
    }

    const getDynamicGroupTeacherLabel = () => {
        if(!groupTeacherOption){
            return ""
        }
        if(groupTeacherOption.chosenPerspective === options.GroupPerspective){
            return "Nauczyciel"
        }
        if(groupTeacherOption.chosenPerspective === options.TeacherPerspective){
            return "Grupa"
        }
        return ""
    }

    useEffect(() => {
        setChosenRoom(null)
        setDynamicGroupTeacherOption(null)
    }, [groupTeacherOption]);

    return <Box className="lesson-constructor">
        <Autocomplete
            className="dynamic-group-teacher-chooser"
            options={getDynamicGroupTeacherOptions()}
            getOptionLabel={getDynamicGroupTeacherOptionLabels}
            value={dynamicGroupTeacherOption}
            onChange={handleDynamicGroupTeacherOption}
            renderInput={(params) => (
                <TextField
                    {...params}
                    label={getDynamicGroupTeacherLabel()}
                />
            )}
            disabled={!groupTeacherOption}
        />
        <Autocomplete
            className="rooom-chooser"
            options={roomData}
            getOptionLabel={option => option.room_code}
            value={chosenRoom}
            onChange={handleRoomChange}
            renderInput={(params) => (
                <TextField
                    {...params}
                    label="Pokój"
                />
            )}
            disabled={!groupTeacherOption}
        />
        <Box
            className="constructed-event"
            draggable={!!chosenRoom && !!dynamicGroupTeacherOption}
            key={chosenRoom?.id + dynamicGroupTeacherOption?.id}
        >
            sieman
        </Box>
    </Box>
}

export default LessonConstructor