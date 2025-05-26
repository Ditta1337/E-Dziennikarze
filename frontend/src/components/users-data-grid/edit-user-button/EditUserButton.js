import {useNavigate} from "react-router";
import {Button} from "@mui/material";

function EditUserButton ( {id} ) {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/admin/list-users/edit-user/${id}`);
    }

    return (
        <Button variant = "contained" size = "small" onClick={handleClick}>
            EDYTUJ
        </Button>
    )

}

export default EditUserButton;