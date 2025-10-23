import ViewGeneratedPlanButton from "./buttons/ViewGeneratedPlanButton";

const columns = [
    {
        field: 'id',
        headerName: "Identyfikator wygenerowanego planu",
        width: 300
    },
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
    {
        field: 'view_plan',
        headerName: "WYŚWIETL PLAN",
        renderCell: (params) => <ViewGeneratedPlanButton id={params.row.id}/>,
        width: 160,
        sortable: false,
        filterable: false,
    }
]

export default columns