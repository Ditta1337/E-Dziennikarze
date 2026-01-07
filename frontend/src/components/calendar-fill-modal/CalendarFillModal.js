import Modal from "../modal/Modal";
import {DateField} from "@mui/x-date-pickers";
import {LocalizationProvider} from "@mui/x-date-pickers/LocalizationProvider";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import {Box, Button, CircularProgress, Typography} from "@mui/material";
import {useState} from "react";
import "./CalendarFillModal.scss"

const CalendarFillModal = ({isOpen, onClose, onClick, fillingCalendar}) => {
    const [from, setFrom] = useState(null)
    const [to, setTo] = useState(null)

    return <Modal className="calendar-fill-modal" isOpen={isOpen} onClose={onClose}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
            <Box className="editor-container">
                <Typography className="editor-title">
                    Podaj od kiedy do kiedy wypełnić kalendarz
                </Typography>

                <Box className="time-fields">
                    <DateField
                        label="Początek"
                        value={from}
                        onChange={(newValue) => setFrom(newValue)}
                    />
                    <DateField
                        label="Koniec"
                        value={to}
                        onChange={(newValue) => setTo(newValue)}
                    />
                </Box>

                <Box className="editor-actions">
                    {fillingCalendar ? <CircularProgress size="small"/>
                        :
                        <Button
                            variant="contained"
                            className="button-fill"
                            onClick={() => onClick(from, to)}
                        >
                            Wypełnij
                        </Button>
                    }
                </Box>
            </Box>
        </LocalizationProvider>
    </Modal>
}

export default CalendarFillModal