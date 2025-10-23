import {useNavigate} from "react-router";
import {Button} from "@mui/material";

function EditUserConfiguration({id}) {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/calendar/generation/config/${id}`)
    }

    return (
        <Button variant="contained" size="small" onClick={handleClick}>
            EDYTUJ
        </Button>
    )

}

export default EditUserConfiguration;