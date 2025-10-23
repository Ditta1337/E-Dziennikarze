import {useEffect, useState} from "react";
import {get, post} from "../../../api"
import CalendarGenerationConfigDataGrid
    from "../../../components/calendar-generation-config-data-grid/CalendarGenerationConfigDataGrid";
import {Box, Button, Popover, TextField} from "@mui/material";
import "./CalendarGenerationConfigList.scss"
import {format} from "date-fns";
import {AppLocale} from "../../../config/localization";
import {useNavigate} from "react-router";

const formatConfigSummary = (configSummary) => {
    return configSummary.map(item => ({
        id: item.id,
        created_at: format(new Date(item.created_at), AppLocale.dateTimeFormat),
        name: item.name,
        office_worker_id: item.office_worker_id,
        calculated: item.calculated,
        calculated_readable: item.calculated ? "TAK" : "NIE",
        person: item.office_worker_name + " " + item.office_worker_surname
    }))
}

const postConfigurationCreation = async (name) => {
    return post("plan/configuration", name, {
        headers: {"Content-Type": "text/plain"}
    });
};


const CalendarGenerationConfigList = () => {
    const navigate = useNavigate();
    const [calendarGenerationConfigSummary, setCalendarGenerationConfigSummary] = useState(null)
    const [newConfigurationName, setNewConfigurationName] = useState(null)
    const [anchorEl, setAnchorEl] = useState(null)

    const fetchCalendarGenerationConfigSummary = async () => {
        try {
            const result = await get("plan/configuration/summary/all")
            setCalendarGenerationConfigSummary(formatConfigSummary(result.data))
        } catch (e) {
            console.log(e)
        }
    }

    const handleOpenCreation = (event) => {
        setAnchorEl(event.currentTarget)
    }

    const handleCloseCreation = () => {
        setAnchorEl(null)
    }

    const handleConfigurationCreation = async () => {
        if (newConfigurationName === null) return //TODO add handling for no name given (snackbar or some shit)
        try {
            const result = await postConfigurationCreation(newConfigurationName)
            navigate(`/calendar/generation/config/${result.data.id}`)
        } catch (e) {
            console.log(e)
        }

    }

    useEffect(() => {
        fetchCalendarGenerationConfigSummary()
    }, []);

    const createConfigurationPopoverOpen = Boolean(anchorEl)
    const id = createConfigurationPopoverOpen ? "function-info-popover" : undefined

    return <Box className="config-list-container">
        <Box className="config-creation-container">
            <Button variant="contained" size="medium" onClick={handleOpenCreation}>
                + Dodaj Konfigurację
            </Button>
        </Box>

        <Box className="config-summary-container">
            {calendarGenerationConfigSummary == null ? <p>Ładowanie</p> :
                <CalendarGenerationConfigDataGrid rows={calendarGenerationConfigSummary}/>}
        </Box>

        <Popover
            id={id}
            open={createConfigurationPopoverOpen}
            anchorEl={anchorEl}
            onClose={handleCloseCreation}
            anchorOrigin={{
                vertical: "bottom",
                horizontal: "center",
            }}
            transformOrigin={{
                vertical: "top",
                horizontal: "center",
            }}
            PaperProps={{
                style: {
                    padding: "0.75rem",
                    maxWidth: 250,
                    backgroundColor: "#f9fafc",
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center"

                },
            }}
        >
            <TextField
                className="configuration-name-input"
                label="Nazwa Konfiguracji"
                variant="outlined"
                value={newConfigurationName}
                onChange={(e) => {
                    setNewConfigurationName(e.target.value)
                    console.log(e.target.value)
                }}
            />
            <Button className="configuration-name-button" variant="contained" size="small"
                    onClick={handleConfigurationCreation}>
                Dodaj
            </Button>
        </Popover>

    </Box>
}

export default CalendarGenerationConfigList