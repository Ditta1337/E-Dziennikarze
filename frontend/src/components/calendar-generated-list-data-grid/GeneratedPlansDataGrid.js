import { Box } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { format } from "date-fns";
import { AppLocale } from "../../config/localization";
import columnsFn from "./columns";

const formatGeneratedPlansSummary = (plansSummary) => {
    return plansSummary.map(item => ({
        id: item.id,
        created_at: format(new Date(item.created_at), AppLocale.dateTimeFormat),
        plan_name: item.plan_name,
        goals: item.goals
    }));
};

const GeneratedPlansDataGrid = ({ generatedPlansSummary }) => {
    if (!generatedPlansSummary || !generatedPlansSummary.length) return null;

    const columns = columnsFn(generatedPlansSummary[0].goals);

    return (
        <Box sx={{height: "min-content" }}>   
            <DataGrid
                rows={formatGeneratedPlansSummary(generatedPlansSummary)}
                columns={columns}
                disableRowSelectionOnClick
                rowHeight={80}
                autoHeight                                      
                sx={{
                    "& .MuiDataGrid-cell": {
                        display: "flex",
                        alignItems: "center",
                    },
                    "& .MuiDataGrid-columnHeaderTitle": {
                        lineHeight: "normal",
                        display: "flex",
                        alignItems: "center",
                    },
                }}
            />
        </Box>
    );
};

export default GeneratedPlansDataGrid;
