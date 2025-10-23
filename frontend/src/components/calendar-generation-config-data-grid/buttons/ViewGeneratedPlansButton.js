import {useNavigate} from "react-router";
import {Button} from "@mui/material";

const ViewGeneratedPlansButton = ({ id, disabled }) => {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/calendar/generated/list/${id}`)
    }

    return (
        <Button variant="contained" size="small" onClick={handleClick} disabled={disabled}>
            WYŚWIETL PLANY
        </Button>
    )
}

export default ViewGeneratedPlansButton