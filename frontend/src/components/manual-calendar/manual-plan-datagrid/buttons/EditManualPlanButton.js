import {useNavigate} from "react-router";
import {Button} from "@mui/material";

const EditManualPlanButton = ( {id} ) => {
    const navigate = useNavigate()

    const handleClick = () => {
        navigate(`/calendar/manual/plan/${id}`)
    }

    return <Button variant="contained" size="small" onClick={handleClick}>
        EDYTUJ
    </Button>
}

export default EditManualPlanButton