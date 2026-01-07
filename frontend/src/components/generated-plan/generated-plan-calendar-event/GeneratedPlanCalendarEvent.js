import {Box, Typography} from "@mui/material";
import "./GeneratedPlanCalendarEvent.scss"

const GeneratedPlanCalendarEvent = ({event}) => {
    const lesson = event.resource;

    return <Box className="event">
        <Typography variant="caption">{event.title}: {lesson.teacher_name}, {lesson.room_code} </Typography>
    </Box>
}

export default GeneratedPlanCalendarEvent