
import ViewGeneratedPlanButton from "./buttons/ViewGeneratedPlanButton";
import { Box, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from "@mui/material";

const generateGoalColumns = (goals) => {
    return goals.map(goal => ({
        field: goal.name,
        headerName: goal.title,
        width: 250,
        sortable: false,
        filterable: false,
        renderCell: (params) => {
            const goalValues = params.row.goals.find(g => g.name === goal.name)?.value || {};
            const entries = Object.entries(goalValues);

            if (entries.length === 0) return null;

            return (
                <TableContainer
                    component={Paper}
                    variant="outlined"
                >
                    <Table size="small" stickyHeader={false}>  
                        <TableHead>
                            <TableRow>
                                {entries.map(([key]) => (
                                    <TableCell
                                        key={key}
                                        align="center"
                                        sx={{ fontWeight: 'bold', padding: '4px' }}
                                    >
                                        {key}
                                    </TableCell>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            <TableRow>
                                {entries.map(([_, value], idx) => (
                                    <TableCell key={idx} align="center" sx={{ padding: '4px' }}>
                                        {value}
                                    </TableCell>
                                ))}
                            </TableRow>
                        </TableBody>
                    </Table>
                </TableContainer>
            );
        }
    }));
};

const columns = (goals) => [
    {
        field: 'plan_name',
        headerName: 'Nazwa wygenerowanego planu',
        width: 250
    },
    {
        field: 'created_at',
        headerName: 'Czas wygenerowania planu',
        width: 200
    },
    ...generateGoalColumns(goals),
    {
        field: 'view_plan',
        headerName: "WYŚWIETL PLAN",
        renderCell: (params) => <ViewGeneratedPlanButton id={params.row.id} />,
        width: 160,
        sortable: false,
        filterable: false,
    }
];

export default columns;
