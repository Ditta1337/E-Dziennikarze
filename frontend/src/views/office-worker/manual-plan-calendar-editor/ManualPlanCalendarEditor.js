import {useNavigate, useParams} from "react-router"
import {get, put, post} from "../../../api"
import React, {useEffect, useState} from "react"
import {Alert, Box, Button, CircularProgress, IconButton, Snackbar, Typography} from "@mui/material"
import GroupTeacherChooser from "../../../components/manual-calendar/group-teacher-chooser/GroupTeacherChooser"
import ManualCalendar from "../../../components/manual-calendar/manual-calendar/ManualCalendar"
import getDatesWeekday from "../../../util/calendar/getDatesWeekday"
import { format } from 'date-fns'
import ErrorIcon from '@mui/icons-material/Error';
import "./ManualPlanCalendarEditor.scss"
import {AppLocale} from "../../../config/localization"
import CreateEditLessonModal
    from "../../../components/manual-calendar/create-edit-lesson-modal/CreateEditLessonModal"
import OverlapErrorsModal from "../../../components/manual-calendar/overlap-errors-modal/OverlapErrorsModal";
import CalendarFillModal from "../../../components/calendar-fill-modal/CalendarFillModal";

const fetchManualPlan = async (id) => {
    return get(`/plan/manual/${id}`)
}

const saveManualPlan = async(id, manualPlan) => {
    return put(`/plan/manual/${id}`, manualPlan)
}

const fillCalender = async (from, to, id) => {
    return post(`/assigned-lesson/fill/manual`, {
        from: format(from, AppLocale.dateFormat),
        to: format(to, AppLocale.dateFormat),
        id: id
    })
}

const makeLessonFromEvent = (event) => {
    return {
        id: !!event.id ? event.id : crypto.randomUUID(),
        active: true,
        group_id: event.groupId,
        group: event.groupCode,
        room_id: event.roomId,
        room: event.room,
        subject_id: event.subjectId,
        subject: event.subject,
        teacher_id: event.teacherId,
        teacher: event.teacher,
        week_day: getDatesWeekday(event.start),
        start_time: format(new Date(event.start), AppLocale.secondTimeFormat),
        end_time: format(new Date(event.end), AppLocale.secondTimeFormat),
    }
}

const ManualPlanCalendarEditor = () => {
    const {id} = useParams()
    const navigate = useNavigate()
    const [manualPlanData, setManualPlanData] = useState(null)
    const [groupTeacherOption, setGroupTeacherOption] = useState(null)
    const [editedEvent, setEditedEvent] = useState(null)
    const [editModalOpen, setEditModalOpen] = useState(false)
    const [creatingEvent, setCreatingEvent] = useState(false)
    const [errorsModalOpen, setErrorsModalOpen] = useState(false)
    const [calendarFillModalOpen, setCalendarFillModalOpen] = useState(false)

    const [snackbarOpen, setSnackbarOpen] = useState(false)
    const [snackbarMessage, setSnackbarMessage] = useState("")
    const [snackbarSeverity, setSnackbarSeverity] = useState("success")

    const updateManualPlanData = async () => {
        try {
            const result = await fetchManualPlan(id)
            setManualPlanData(result.data)
        } catch (e) {
            displaySnackbarMessage("Wystąpił błąd podczas pobierania planu.")
        }
    }

    const handlePlanSave = async () => {
        console.log(manualPlanData)
        try{
            const result = await saveManualPlan(id, manualPlanData.lessons)
            setManualPlanData(result.data)
            console.log(result.data)
            if(result.data.errors !== null && result.data.errors.length !== 0) {
                setErrorsModalOpen(true)
                displaySnackbarMessage("Zapisano zmodyfikowany plan z konfliktami")
            } else {
                displaySnackbarMessage("Zapisano zmodyfikowany plan bez konfliktów.", false)
            }
        } catch(e) {
            displaySnackbarMessage("Wystąpił błąd podczas zapisywania planu.")
        }
    }

    const displaySnackbarMessage = (message, isErrorMessage = true) => {
        setSnackbarMessage(message)
        if(isErrorMessage) {
            setSnackbarSeverity("error")
        } else{
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

    const editLesson = (event) => {
        const newLesson = makeLessonFromEvent(event)
        console.log(newLesson)
        const lessonsWithoutModified = manualPlanData.lessons.filter(lesson => lesson.id !== newLesson.id)
        setManualPlanData({...manualPlanData,
            lessons : [...lessonsWithoutModified, newLesson]
        })
    }

    const deleteLesson = (event) => {
        const lessonWithoutEvent = manualPlanData.lessons.filter(lesson => lesson.id !== event.id)
        setManualPlanData({...manualPlanData,
            lessons : lessonWithoutEvent
        })
    }

    const handleEventSelect = (event) => {
        setEditedEvent(event)
        setCreatingEvent(false)
        setEditModalOpen(true)
    }

    const handleEventCreation = ({start, end}) => {
        setEditedEvent({
            start: start,
            end: end
        })
        setCreatingEvent(true)
        setEditModalOpen(true)
    }

    const handleGoBackToCalculation = () => {
        navigate(`/calendar/generated/plan/${manualPlanData.plan_calculation_id}`)
    }

    const onFillerModalConfirm = async (from, to) => {
        try{
            await fillCalender(from, to, id)
            displaySnackbarMessage("Pomyślnie wypełniono kalendarz.", true)
        } catch (e){
            displaySnackbarMessage("Wystąpił błąd podczas wypełniania kalendarza.")
        }
        setCalendarFillModalOpen(false)
    }

    useEffect(() => {
        updateManualPlanData()
    }, [])

    useEffect(() => {
        console.log(manualPlanData)
    }, [manualPlanData])

    if (!manualPlanData) {
        return <Box className="loading">
            <CircularProgress/>
        </Box>
    }

    return (
        <Box className="manual-calendar-editor">
            <Typography className="title">
                Ręczna edycja planu: {!!manualPlanData ? manualPlanData.name : "..."}
            </Typography>
            <Box className="chooser-actions-container">
                <GroupTeacherChooser
                    groupData={manualPlanData.groups}
                    teacherData={manualPlanData.teachers}
                    onGroupTeacherOptionChange={setGroupTeacherOption}
                />
                <Box className="go-to-generation-action">
                    {manualPlanData.plan_calculation_id ? (
                        <Button variant="contained" onClick={handleGoBackToCalculation}>
                            Przejdź do oryginalnej generacji
                        </Button>
                    ) : null}
                </Box>
                <Box className="save-generate-error-actions">
                    {manualPlanData.errors !== null && manualPlanData.errors.length !== 0 ?
                        <IconButton size="small" onClick={() => setErrorsModalOpen(true)}>
                            <ErrorIcon />
                        </IconButton> : null
                    }
                    <Button variant="contained" onClick={handlePlanSave}>
                        Zapisz
                    </Button>
                    <Button variant="contained" onClick={() => setCalendarFillModalOpen(true)}>
                        Wypełnij kalendarz
                    </Button>
                </Box>
            </Box>
            <Box className="calendar-container">
                <ManualCalendar
                    lessonData={manualPlanData.lessons}
                    editLesson={editLesson}
                    perspective={groupTeacherOption}
                    handleEventSelect={handleEventSelect}
                    handleEventCreation={handleEventCreation}
                    displaySnackbarMessage={displaySnackbarMessage}
                />
            </Box>

            <CreateEditLessonModal
                event={editedEvent}
                perspective={groupTeacherOption}
                groupData={manualPlanData.groups}
                teacherData={manualPlanData.teachers}
                subjectData={manualPlanData.subjects}
                roomData={manualPlanData.rooms}
                isOpen={editModalOpen}
                onClose={() => {
                    setEditModalOpen(false)
                    setEditedEvent(null)
                }}
                isCreated={creatingEvent}
                saveEvent={editLesson}
                deleteEvent={deleteLesson}
            />

            <OverlapErrorsModal
                isOpen={errorsModalOpen}
                onClose={() => setErrorsModalOpen(false)}
                message={manualPlanData.errors}
            />

            <CalendarFillModal
                isOpen={calendarFillModalOpen}
                onClose={() => setCalendarFillModalOpen(false)}
                onClick={onFillerModalConfirm}
            />

            <Snackbar
                open={snackbarOpen}
                autoHideDuration={6000}
                onClose={handleSnackbarClose}
                anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
            >
                <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{ width: '100%' }}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>

        </Box>
    )

}

export default ManualPlanCalendarEditor