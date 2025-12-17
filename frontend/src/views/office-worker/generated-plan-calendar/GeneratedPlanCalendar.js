import {useNavigate, useParams} from "react-router";
import {get, post} from "../../../api"
import "./GeneratedPlanCalendar.scss"
import {Alert, Box, Button, Snackbar, Typography} from "@mui/material";
import GeneratedPlanBrowsableCalendar
    from "../../../components/generated-plan/generated-plan-browsable-calendar/GeneratedPlanBrowsableCalendar";
import GroupTeacherStudentChooser
    from "../../../components/generated-plan/group-teacher-student-chooser/GroupTeacherStudentChooser";
import {StudentRole, TeacherRole} from "../../admin/roles";
import React, {useEffect, useState} from "react";
import ButtonTextInputPopover from "../../../components/button-text-input-popover/ButtonTextInputPopover";
import CalendarFillModal from "../../../components/calendar-fill-modal/CalendarFillModal";
import {format} from "date-fns";
import {AppLocale} from "../../../config/localization";

const fetchGeneratedPlan = async (id) => {
    return get(`plan/calculation/plan/${id}`)
}

const fetchGroupSubjet = async () => {
    return get("group-subject/all")
}

const fetchGroups = async () => {
    return get("group/all")
}

const fetchTeachers = async () => {
    return get(`user/all/active/${TeacherRole}`)
}

const fetchStudents = async () => {
    return get(`user/all/active/${StudentRole}`);
}

const fetchStudentsGroups = async (id) => {
    return get(`/student-group/student/${id}`)
}

const fetchSubjects = async () => {
    return get(`/subject/all`)
}

const fetchRooms = async () => {
    return get(`/room/all`)
}

const copyGeneratedPlanToManualEdit = async (id, name) => {
    return post(`plan/manual/copy/calculation/${id}`, name, {
        headers: {"Content-Type": "text/plain"}
    });
}

const fillCalender = async (id, from, to) => {
    return post(`/assigned-lesson/fill/generated`, {
        from: format(from, AppLocale.dateFormat),
        to: format(to, AppLocale.dateFormat),
        id: id
    })
}

const GeneratedPlanCalendar = () => {
    const {id} = useParams()
    const navigate = useNavigate();
    const [groupsToDisplay, setGroupsToDisplay] = useState([])
    const [teacherToDisplay, setTeacherToDisplay] = useState(null)
    const [generatedPlanData, setGeneratedPlanData] = useState(null)
    const [calendarFillModalOpen, setCalendarFillModalOpen] = useState(false)

    const [snackbarOpen, setSnackbarOpen] = useState(false)
    const [snackbarMessage, setSnackbarMessage] = useState("")
    const [snackbarSeverity, setSnackbarSeverity] = useState("success")

    const handleManualEdit = async (manualEditName) => {
        if(manualEditName === null) return
        try{
            const result = await copyGeneratedPlanToManualEdit(id, manualEditName)
            navigate(`/calendar/manual/plan/${result.data}`)
        } catch (e) {
            displaySnackbarMessage("Wystąpił błąd kopiowania wygenerowanego planu!")
        }
    }

    const updateGeneratedPlanData = async () => {
        try{
            const result = await fetchGeneratedPlan(id)
            setGeneratedPlanData(result.data)
        } catch(e) {
            displaySnackbarMessage("Wystąpił problem podczas pobierania wygenerowanego planu!")
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

    const onFillerModalConfirm = async (from, to) => {
        try{
            fillCalender(id, from, to)
            displaySnackbarMessage("Pomyślnie wypełniono kalendarz.", false)
        } catch (e){
            displaySnackbarMessage("Wystąpił błąd podczas wypełniania kalendarza.")
        }
        setCalendarFillModalOpen(false)
    }

    useEffect(() => {
        updateGeneratedPlanData()
    }, []);

    return <Box className="generated-plan-calendar">

        <Typography className="title">
            Wygenerowany plan: {generatedPlanData != null ? generatedPlanData.name : "..."}
        </Typography>

        <Box className="chooser">
            <GroupTeacherStudentChooser
                fetchGroupSubject={fetchGroupSubjet}
                fetchStudents={fetchStudents}
                fetchGroups={fetchGroups}
                fetchTeachers={fetchTeachers}
                fetchStudentsGroups={fetchStudentsGroups}
                setGroupsToDisplay={setGroupsToDisplay}
                setTeacherToDisplay={setTeacherToDisplay}
            />
            <Box className="button-popover-wrapper">
                <ButtonTextInputPopover
                    popoverOpenButtonText="Stwórz edycję manualną"
                    popoverInnerButtonText="Stwórz edycję"
                    popoverTextFieldLabel="Nazwa manualnej edycji"
                    handlePopoverButtonClick={handleManualEdit}
                />
                <Button variant="contained" onClick={() => setCalendarFillModalOpen(true)}>
                    Wypełnij kalendarz
                </Button>
            </Box>
        </Box>

        <Box className="browsable-calendar">
            <GeneratedPlanBrowsableCalendar
                generatedPlanData={generatedPlanData}
                fetchTeachers={fetchTeachers}
                fetchGroups={fetchGroups}
                fetchSubjets={fetchSubjects}
                fetchRooms={fetchRooms}
                groupsToDisplay={groupsToDisplay}
                teacherToDisplay={teacherToDisplay}
                displaySnackbarMessage={displaySnackbarMessage}
            />
        </Box>

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

}

export default GeneratedPlanCalendar