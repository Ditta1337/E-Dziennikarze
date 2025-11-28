import Modal from "../../modal/Modal";
import {Typography} from "@mui/material";
import "./OverlapErrorsModal.scss"

const OverlapErrorsModal = ({message, isOpen, onClose}) => {
    if(!message || message === "") return null

    return <Modal
        className="overlap-errors-modal"
        isOpen={isOpen}
        onClose={onClose}
    >
        {message.map((mess, i) => (
            <Typography className="error-message">
                {`${i+1}: ${mess}`}
                <br/>
                <br/>
            </Typography>
        ))}
    </Modal>
}

export default OverlapErrorsModal