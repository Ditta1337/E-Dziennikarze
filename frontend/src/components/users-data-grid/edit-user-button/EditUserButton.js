import {useNavigate} from "react-router";
import {Button} from "@mui/material";

function EditUserButton ( {id} ) {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/edit-user/${id}`);
    }

    return (
        <Button variant = "contained" size = "small" onClick={handleClick}>
            EDIT
        </Button>
    )

}

export default EditUserButton;