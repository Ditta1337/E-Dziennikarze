import {useStore} from "../../../store"
import {get} from "../../../api"
import React, {useCallback, useEffect, useState} from "react"
import GuardiansStudentChooser from "../../../components/grade-list/guardians-student-chooser/GuardiansStudentChooser";
import {Alert, Box, CircularProgress, Snackbar} from "@mui/material";
import "./GuardianCalendar.scss"
import WeeklyReadOnlyCalendar from "../../../components/calendar/weekly-read-only-calendar/WeeklyReadOnlyCalendar";
import StudentLessonDetails from "../../../components/calendar/lesson-details/student/StudentLessonDetails";

const fetchStudentsForGuardian = async (guardianId) => {
    return get(`/student-guardian/guardian/${guardianId}`)
}

const fetchLessons = async (userId, startDate, endDate) => {
    return get(`/lesson/all/student/${userId}/from/${startDate}/to/${endDate}`)
}

const GuardianCalendar = () => {
    const guardianId = useStore((state) => state.user.userId);
    const [students, setStudents] = useState([]);
    const [selectedStudentId, setSelectedStudentId] = useState('');

    const [selectedEvent, setSelectedEvent] = useState(null);

    const [loading, setLoading] = useState(true);

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");


    const updateStudents = useCallback(async () => {
        if (!guardianId) {
            setLoading(false)
            return
        }
        setLoading(true)
        try {
            const response = await fetchStudentsForGuardian(guardianId)
            const studentData = response.data
            setStudents(studentData)

            if (studentData.length > 0) {
                setSelectedStudentId(studentData[0].id)
            }
        } catch (error) {
            console.error("Error fetching students for guardian:", error)
            setSnackbarMessage("Wystąpił błąd podczas pobierania listy uczniów")
            setSnackbarSeverity("error")
            setSnackbarOpen(true)
        } finally {
            setLoading(false)
        }
    }, [guardianId])

    const handleStudentChange = (event) => {
        console.log(event.target.value)
    }

    const displaySnackbarMessage = (message, isErrorMessage = true) => {
        setSnackbarMessage(message)
        if (isErrorMessage) {
            setSnackbarSeverity("error")
        } else {
            setSnackbarSeverity("success")
        }
        setSnackbarOpen(true)
    }

    const handleSnackbarClose = (event, reason) => {
        if (reason === "clickaway") {
            return
        }
        setSnackbarOpen(false)
    }

    useEffect(() => {
        updateStudents()
    }, [updateStudents]);

    if (loading) {
        return <Box className="guardian-calendar">
            <CircularProgress/>
        </Box>
    }

    return <Box className="guardian-calendar">
        <GuardiansStudentChooser
            selectedStudentId={selectedStudentId}
            handleStudentChange={handleStudentChange}
            students={students}
        />
        <WeeklyReadOnlyCalendar
            userId={selectedStudentId}
            onSelectEvent={setSelectedEvent}
            fetchLessons={fetchLessons}
        />
        <StudentLessonDetails
            isOpen={!!selectedEvent}
            onClose={() => setSelectedEvent(null)}
            event={selectedEvent}
        />
        <Snackbar
            open={snackbarOpen}
            autoHideDuration={6000}
            onClose={handleSnackbarClose}
            anchorOrigin={{vertical: 'bottom', horizontal: 'left'}}
        >
            <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{width: '100%'}}>
                {snackbarMessage}
            </Alert>
        </Snackbar>
    </Box>
}

export default GuardianCalendar