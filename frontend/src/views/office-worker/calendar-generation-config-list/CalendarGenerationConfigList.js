import {useEffect, useState} from "react";
import {get, post} from "../../../api"
import CalendarGenerationConfigDataGrid
    from "../../../components/calendar-generation-config-data-grid/CalendarGenerationConfigDataGrid";
import {Box} from "@mui/material";
import "./CalendarGenerationConfigList.scss"
import {format} from "date-fns";
import {AppLocale} from "../../../config/localization";
import {useNavigate} from "react-router";
import ButtonTextInputPopover from "../../../components/button-text-input-popover/ButtonTextInputPopover";

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

    const fetchCalendarGenerationConfigSummary = async () => {
        try {
            const result = await get("plan/configuration/summary/all")
            setCalendarGenerationConfigSummary(formatConfigSummary(result.data))
        } catch (e) {
            console.log(e)
        }
    }

    const handleConfigurationCreation = async (newConfigurationName) => {
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

    return <Box className="config-list-container">
        <Box className="config-creation-container">
            <ButtonTextInputPopover
                popoverOpenButtonText="Dodaj Konfigurację"
                popoverInnerButtonText="Dodaj"
                popoverTextFieldLabel="Nazwa Konfiguracji"
                handlePopoverButtonClick={handleConfigurationCreation}
            />
        </Box>

        <Box className="config-summary-container">
            {calendarGenerationConfigSummary == null ? <p>Ładowanie</p> :
                <CalendarGenerationConfigDataGrid rows={calendarGenerationConfigSummary}/>}
        </Box>
    </Box>
}

export default CalendarGenerationConfigList