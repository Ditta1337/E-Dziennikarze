import {useNavigate} from "react-router";
import {Button} from "@mui/material";

const ViewGeneratedPlanButton = ({id}) => {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/calendar/generated/plan/${id}`)
    }

    return (
        <Button variant="contained" size="small" onClick={handleClick}>
            WYŚWIETL
        </Button>
    )
}

export default ViewGeneratedPlanButton