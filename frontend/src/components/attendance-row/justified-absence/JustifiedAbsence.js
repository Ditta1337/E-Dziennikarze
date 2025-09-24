import MoreVertIcon from "@mui/icons-material/MoreVert";
import {Button, Menu, MenuItem} from "@mui/material";
import {useState} from "react";
import {absenceJustificationTypes} from "./AbsenceJustificationTypes"
import "./JustifiedAbsence.scss"

const JustifiedAbsence = ({value, onSelect}) => {
    const [anchorEl, setAnchorEl] = useState(null)

    const handleOpen = (event) => setAnchorEl(event.currentTarget)
    const handleClose = () => setAnchorEl(null);

    const handleSelect = (type) => {
        onSelect(type)
        handleClose()
    }

    const selected = absenceJustificationTypes.find(type => type.value === value)

    return (
        <>
            <Button
                className="justification-button"
                variant="outlined"
                endIcon={<MoreVertIcon/>}
                onClick={handleOpen}
                size="small">
                {selected ? selected.label : "Usprawiedliwienie"}
            </Button>
            <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleClose}>
                {absenceJustificationTypes.map((type) => (
                    <MenuItem onClick={() => handleSelect(type.value)}>{type.label}</MenuItem>
                ))}
            </Menu>
        </>
    )
}

export default JustifiedAbsence