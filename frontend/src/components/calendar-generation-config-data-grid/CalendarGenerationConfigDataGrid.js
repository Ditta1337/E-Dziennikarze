import {DataGrid} from "@mui/x-data-grid";
import columns from "./columns";
import {Box} from "@mui/material";

const CalendarGenerationConfigDataGrid = ({rows}) => {
    return <Box>
        <DataGrid
            rows={rows}
            columns={columns}
            showToolbar
            disableRowSelectionOnClick
        />
    </Box>
}

export default CalendarGenerationConfigDataGrid