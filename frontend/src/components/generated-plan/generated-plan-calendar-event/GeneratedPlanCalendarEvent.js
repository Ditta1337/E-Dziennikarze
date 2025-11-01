import {Box, Typography} from "@mui/material";
import "./GeneratedPlanCalendarEvent.scss"

const GeneratedPlanCalendarEvent = ({event}) => {
    const lesson = event.resource;

    return <Box className="event">
        <Typography variant="caption">{event.title}</Typography>
        <Box className="teacher-room">
            <Typography variant="caption">{lesson.teacher_name}</Typography>
            <Typography variant="caption">{lesson.room_code}</Typography>
        </Box>
    </Box>
}

export default GeneratedPlanCalendarEvent