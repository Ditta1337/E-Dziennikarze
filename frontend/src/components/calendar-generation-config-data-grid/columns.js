import EditUserConfiguration from "./buttons/EditConfigurationButton";
import ViewGeneratedPlansButton from "./buttons/ViewGeneratedPlansButton";

const columns = [
    {
        field: 'edit',
        headerName: 'EDYTUJ',
        renderCell: (params) => <EditUserConfiguration id={params.row.id}/>,
        sortable: false,
        filterable: false,
    },
    {
        field: 'name',
        headerName: 'Nazwa konfiguracji',
        width: 200
    },
    {
        field: 'person',
        headerName: 'Twórca',
        width: 300
    },
    {
        field: 'created_at',
        headerName: 'Czas stworzenia',
        width: 250
    },
    {
        field: 'calculated_readable',
        headerName: 'Wygenerowano',
        width: 150
    },
    {
        field: 'view_plans',
        headerName: 'WYŚWIETL PLANY',
        renderCell: (params) => <ViewGeneratedPlansButton id={params.row.id} disabled={!params.row.calculated} />,
        sortable: false,
        filterable: false,
        width: 155
    }
]

export default columns