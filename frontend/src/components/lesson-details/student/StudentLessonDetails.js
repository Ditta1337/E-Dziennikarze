import Modal from "../../modal/Modal";
import { Typography } from "@mui/material";
import { AppLocale } from "../../../config/localization";
import { format } from "date-fns";
import { useEffect, useState } from "react";
import { get } from "../../../api";
import "./StudentLessonDetails.scss"

const StudentLessonDetails = ({ event, isOpen, onClose }) => {
    const [teacher, setTeacher] = useState(null);

    useEffect(() => {
        if (!event) return; // safe early exit inside effect

        const fetchTeacher = async () => {
            try {
                const result = await get(`/user/${event.teacherId}`);
                setTeacher(`${result.data.name} ${result.data.surname}`);
            } catch (e) {
                setTeacher("Nie znaleziono");
            }
        };

        fetchTeacher();
    }, [event]);

    if (!event) return null

    return (
        <Modal
            className="student-lesson-details"
            isOpen={isOpen}
            onClose={onClose}
        >
            <Typography className="time">
                {`Czas trwania: ${format(event.start, AppLocale.timeFormat)} - ${format(event.end, AppLocale.timeFormat)}`}
            </Typography>
            <Typography className="lesson">
                {`Lekcja: ${event.title}`}
            </Typography>
            <Typography className="teacher">
                {`Nauczyciel: ${teacher}` || "Ładowanie..."}
            </Typography>
            <Typography className="room">
                {`Sala: ${event.room}`}
            </Typography>
        </Modal>
    );
};

export default StudentLessonDetails;
