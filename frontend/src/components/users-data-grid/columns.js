import {Avatar} from "@mui/material";
import ViewButton from "./edit-user-button/EditUserButton"

const columns = [
    {
        field: 'edit',
        headerName: 'EDIT',
        renderCell: (params) => <ViewButton id={params.row.id}/>,
        sortable: false,
        filterable: false,
    },
    {
        field: 'image_base64',
        headerName: 'Icon',
        renderCell: (params) => (
            <Avatar alt={params.row.name} src={params.value} />
        ),
        filterable: false,
        sortable: false,
        width: 60
    },
    {
        field: 'id',
        headerName: 'ID',
        width: 280
    },
    {
        field: 'name',
        headerName: 'Name',
    },
    {
        field: 'surname',
        headerName: 'Surname',
    },
    {
        field: 'created_at',
        headerName: 'Created At',
        width: 170
    },
    {
        field: 'address',
        headerName: 'Address',
        minWidth: 100,
        flex: 1
    },
    {
        field: 'email',
        headerName: 'Email',
        minWidth: 150,
        flex: 2,
    },
    {
        field: 'contact',
        headerName: 'Contact',
        minWidth: 150,
        flex: 1,
    },
    {
        field: 'role',
        headerName: 'Role',
        width: 130,
    },
    {
        field: 'active',
        headerName: 'Active',
    },
];

export default columns;