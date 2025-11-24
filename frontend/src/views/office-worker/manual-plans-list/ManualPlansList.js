import {get, post} from "../../../api"
import {useEffect, useState} from "react";
import {Box} from "@mui/material";
import ManualPlanDataGrid from "../../../components/manual-calendar/manual-plan-datagrid/ManualPlanDataGrid";
import "./ManualPlansList.scss"
import ButtonTextInputPopover from "../../../components/button-text-input-popover/ButtonTextInputPopover";
import {useNavigate} from "react-router";

const fetchManualPlansSummary = async () => {
    return get("plan/manual/summary")
}

const postNewEmptyPlan = async (name) => {
    return post("plan/manual", name, {
        headers: {"Content-Type": "text/plain"}
    })
}

const formatManualPlanSummary = (manualPlanSummary) => {
    return manualPlanSummary.map(summary => ({
        id: summary.id,
        name: summary.name,
        person: summary.office_worker_name + " " + summary.office_worker_surname,
        created_at: summary.created_at,
    }))
}

const ManualPlansList = () => {
    const navigate = useNavigate()
    const [manualPlanSummary, setManualPlanSummary] = useState(null)

    const updateManualPlansSummary = async () => {
        try {
            const result = await fetchManualPlansSummary()
            setManualPlanSummary(formatManualPlanSummary(result.data))
            console.log(formatManualPlanSummary(result.data))
            console.log(result.data)
        } catch (e) {
            console.error(e)
        }
    }

    const handleManualPlanCreation = async (name) => {
        try {
            const result = await postNewEmptyPlan(name)
            navigate(`/calendar/manual/plan/${result.data}`)
        } catch (e) {
            console.log(e)
        }
    }

    useEffect(() => {
        updateManualPlansSummary()
    }, []);

    return <Box className="manual-plan-list">
        <Box className="manual-plan-create-container">
            <ButtonTextInputPopover
                popoverOpenButtonText="Dodaj ręcznie tworzony plan"
                popoverInnerButtonText="Dodaj"
                popoverTextFieldLabel="Nazwa planu"
                handlePopoverButtonClick={handleManualPlanCreation}
            />
        </Box>
        <Box className="manual-plan-summary-container">
            <ManualPlanDataGrid rows={manualPlanSummary}/>
        </Box>
    </Box>
}

export default ManualPlansList