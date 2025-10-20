import EditUserConfiguration from "./edit-configuration-button/EditConfigurationButton";

const columns = [
    {
        field: 'edit',
        headerName: 'EDIT',
        renderCell: (params) => <EditUserConfiguration id={params.row.id}/>,
        sortable: false,
        filterable: false,
    },
    {
        field: 'name',
        headerName: 'Nazwa konfiguracji',
        width: 300
    },
    {
        field: 'person',
        headerName: 'Twórca',
        width: 300
    },
    {
        field: 'created_at',
        headerName: 'Czas stworzenia',
        width: 300
    },
    {
        field: 'calculated',
        headerName: 'Wygenerowano plan zajęć',
        width: 300
    }
]

export default columns