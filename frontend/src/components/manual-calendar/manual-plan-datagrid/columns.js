import EditConfigurationButton from "../../calendar-generation-config-data-grid/buttons/EditConfigurationButton";
import EditManualPlanButton from "./buttons/EditManualPlanButton";

const columns = [
    {
        field: "edit",
        headerName: "EDYTUJ",
        renderCell: (params) => <EditManualPlanButton id={params.id} />,
        sortable: false,
        filterable: false,
        width: 170
    },
    {
        field: "name",
        headerName: "Nazwa planu",
        width: 200
    },
    {
        field: "person",
        headerName: "Autor",
        width: 200
    },
    {
        field: 'created_at',
        headerName: 'Czas stworzenia',
        width: 250
    }
]

export default columns