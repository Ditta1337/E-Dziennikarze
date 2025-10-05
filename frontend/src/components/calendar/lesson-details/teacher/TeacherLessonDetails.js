import Modal from "../../../modal/Modal";
import {Button, Typography} from "@mui/material";
import {format} from "date-fns";
import {AppLocale} from "../../../../config/localization";
import "./TeacherLessonDetails.scss"

const TeacherLessonDetails = ({event, setCheckAttendanceModalOpen, isOpen, onClose}) => {
    if (!event) return null

    return (
        <Modal
            className="teacher-lesson-details"
            isOpen={isOpen}
            onClose={onClose}
        >
            <Typography className="time">
                {`Czas trwania: ${format(event.start, AppLocale.timeFormat)} - ${format(event.end, AppLocale.timeFormat)}`}
            </Typography>
            <Typography className="lesson">
                {`Lekcja: ${event.title}`}
            </Typography>
            <Typography className="room">
                {`Sala: ${event.room}`}
            </Typography>
            <Button
                className="attendance-button"
                variant="contained"
                onClick={() => setCheckAttendanceModalOpen(true)}
            >
                Sprawdź obecność
            </Button>
        </Modal>
    )
}

export default TeacherLessonDetails