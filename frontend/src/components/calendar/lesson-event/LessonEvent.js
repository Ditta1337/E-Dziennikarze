import {Box, Typography} from "@mui/material";
import "./LessonEvent.scss"

const LessonEvent = ({ event }) => {

    return <Box className="event">
        <Typography>{event.title}, {event.room}</Typography>
    </Box>
}

export default LessonEvent