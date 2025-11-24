import GoalFunctionList from "../../../components/calendar-generation-config/goal-function-list/GoalFunctionList"
import {Alert, Box, Button, Snackbar, Typography} from "@mui/material"
import "./CalendarGenerationConfig.scss"
import React, {useEffect, useState} from "react"
import {get, put, post} from "../../../api"
import GroupSubjectForm from "../../../components/calendar-generation-config/group-subject-form/GroupSubjectForm"
import {useNavigate, useParams} from "react-router"
import ButtonTextInputPopover from "../../../components/button-text-input-popover/ButtonTextInputPopover";

const fetchGoalFunctions = async () => {
    return get("solver/goal/functions")
}

const saveConfigurationData = async (configurationData) => {
    return put("plan/configuration", configurationData)
}

const fetchGroupSubjectData = async () => {
    return get("group-subject/all")
}

const fetchRooms = async () => {
    return get("room/all")
}

const postConfigurationCopy = async (configurationId, newName) => {
    return post(`plan/configuration/copy/${configurationId}`, newName , {
            headers: {"Content-Type": "text/plain"}
        }
    )
}

const postEnqueuePlan = async (planConfiguration) => {
    return post("plan/enqueue", planConfiguration)
}

const CalendarGenerationConfig = () => {
    const navigate = useNavigate()
    const {id} = useParams()
    const [configurationData, setConfigurationData] = useState(null)
    const [anchorEl, setAnchorEl] = useState(null)

    const [snackbarOpen, setSnackbarOpen] = useState(false)
    const [snackbarMessage, setSnackbarMessage] = useState("")
    const [snackbarSeverity, setSnackbarSeverity] = useState("success")

    const fetchCalendarConfigurationData = async (id) => {
        try {
            const result = await get(`plan/configuration/${id}`)
            return result.data
        } catch (e) {
            console.error(e)
            displaySnackbarMessage("Wystąpił błąd podczas pobierania danych konfiguracji.")
        }
    }

    const handleOpenCopy = (event) => {
        setAnchorEl(event.currentTarget)
    }

    const handleCloseCopy = () => {
        setAnchorEl(null)
    }

    const handleConfigurationCopy = async (newConfigurationName) => {
        if (newConfigurationName === null) {
            displaySnackbarMessage("Wpisz nazwę konfiguracji!")
            return
        }
        try {
            const result = await postConfigurationCopy(id, newConfigurationName)
            navigate(`/calendar/generation/config/${result.data.id}`)
            handleCloseCopy()
            displaySnackbarMessage(`Pomyślnie skopiowano konfigurację ${configurationData.name} do nowej konfiguracji ${newConfigurationName}`, false)
        } catch (e) {
            console.error(e)
            displaySnackbarMessage("Wystąpił błąd podczas kopiowania konfiguracji.")
        }

    }

    const handlePlanGeneration = async () => {
        try{
            await postEnqueuePlan(configurationData.configuration)
            navigate(`/calendar/generated/list/${id}`)
        } catch (e) {
            console.error(e)
            displaySnackbarMessage("Wystąpił błąd podczas generacji planu lekcji.")
        }
    }

    const handleConfigurationSave = async () => {
        try {
            await saveConfigurationData(configurationData)
            displaySnackbarMessage("Zapisano konfigurację!", false)
        } catch (e) {
            console.error(e)
            displaySnackbarMessage("Wystąpił błąd podczas zapisywania konfiguracji.")
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

    useEffect(() => {
        const loadConfigData = async () => {
            const data = await fetchCalendarConfigurationData(id)
            setConfigurationData(data)
        }

        loadConfigData()
    }, [id])

    useEffect(() => {
        console.log(configurationData)
    }, [configurationData]);

    const copyConfigurationPopoverOpen = Boolean(anchorEl)

    return <>
        <Box className="calendar-generation-config">
            <Typography className="title">{configurationData ? configurationData.name : "Ładowanie..."}</Typography>
            <Box className="copy-button-title-container">
                <ButtonTextInputPopover
                    popoverOpenButtonText="Kopiuj konfigurację"
                    popoverInnerButtonText="Kopiuj"
                    popoverTextFieldLabel="Nazwa Konfiguracji"
                    handlePopoverButtonClick={handleConfigurationCopy}
                />
            </Box>
            <Box className="config-input">
                <Box className="groups-subjects">
                    <GroupSubjectForm configurationData={configurationData} setConfigurationData={setConfigurationData}
                                      fetchGroupSubjectData={fetchGroupSubjectData} fetchRooms={fetchRooms} displaySnackBarMessage={displaySnackbarMessage}/>
                </Box>
                <Box className="goal-functions">
                    <GoalFunctionList configurationData={configurationData} setConfigurationData={setConfigurationData} fetchGoalFunctions={fetchGoalFunctions} displaySnackbarMessage={displaySnackbarMessage}/>
                </Box>
            </Box>
            <Box className="save-generate">
                <Button onClick={handleConfigurationSave}
                        variant="contained"
                        className="save"
                        disabled={configurationData?.calculated || false}
                >
                    Zapisz
                </Button>
                <Button onClick={handlePlanGeneration}
                        variant="contained"
                        className="generate"
                        disabled={configurationData?.calculated || false}
                >
                    Generuj
                </Button>
            </Box>

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
    </>
}

export default CalendarGenerationConfig