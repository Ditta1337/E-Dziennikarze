import Modal from "../../modal/Modal";
import {Box, Button, Typography} from "@mui/material";
import {LocalizationProvider} from "@mui/x-date-pickers/LocalizationProvider";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import {TimeField} from "@mui/x-date-pickers/TimeField";
import dayjs from "dayjs";
import "dayjs/locale/pl";
import {useEffect, useState} from "react";
import "./UnavailableEventEditor.scss";

const UnavailableEventEditor = ({event, isOpen, onClose, onEdit, onDelete}) => {
    const [startTime, setStartTime] = useState(null);
    const [endTime, setEndTime] = useState(null);

    useEffect(() => {
        if (!event) return;
        setStartTime(dayjs(event.start));
        setEndTime(dayjs(event.end));
    }, [event]);

    const handleEdit = () => {
        if (onEdit && startTime && endTime) {
            onEdit({
                "event": event,
                "start": startTime.toDate(),
                "end": endTime.toDate()
            })
        }
        onClose()
    }

    const handleDelete = () => {
        onDelete(event)
        onClose()
    }

    return (
        <Modal className="unavailable-event-editor" isOpen={isOpen} onClose={onClose}>
            <div className="editor-container">
                <Typography variant="h6" className="editor-title">
                    Edytuj niedostępność
                </Typography>

                <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="pl">
                    <Box className="time-fields">
                        <TimeField
                            label="Początek"
                            value={startTime}
                            format="HH:mm"
                            onChange={(newValue) => setStartTime(newValue)}
                        />
                        <TimeField
                            label="Koniec"
                            value={endTime}
                            format="HH:mm"
                            onChange={(newValue) => setEndTime(newValue)}
                        />
                    </Box>
                </LocalizationProvider>

                <div className="editor-actions">
                    <Button variant="outlined" color="inherit" onClick={onClose}>
                        Anuluj
                    </Button>
                    <Button variant="contained" className="button-edit" onClick={() => handleEdit()}>
                        Edytuj
                    </Button>
                    <Button variant="contained" className="button-delete" onClick={() => handleDelete()}>
                        Usuń
                    </Button>
                </div>
            </div>
        </Modal>
    );
};

export default UnavailableEventEditor;
