import {DataGrid} from '@mui/x-data-grid';
import columns from './columns';

function UsersDataGrid({ rows }) {
    return <div>
        <DataGrid
            rows={rows}
            columns={columns}
            showToolbar
            disableRowSelectionOnClick
        />
    </div>
}

export default UsersDataGrid;