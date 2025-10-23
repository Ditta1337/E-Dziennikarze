import {Box} from "@mui/material";
import {DataGrid} from "@mui/x-data-grid";
import columns from "./columns"
import {format} from "date-fns";
import {AppLocale} from "../../config/localization";

const formatGeneratedPlansSummary = (plansSummary) => {
    return plansSummary.map(item => ({
        id: item.id,
        created_at: format(new Date(item.created_at), AppLocale.dateTimeFormat),
        plan_name: item.plan_name,
    }))
}

const GeneratedPlansDataGrid = ({generatedPlansSummary}) => {

    return <Box>
        <DataGrid
            rows={formatGeneratedPlansSummary(generatedPlansSummary)}
            columns={columns}
            showToolbar
            disableRowSelectionOnClick
        />
    </Box>

}

export default GeneratedPlansDataGrid