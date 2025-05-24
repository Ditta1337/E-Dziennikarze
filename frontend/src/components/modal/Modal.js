import {Modal as MUIModal, Box, IconButton} from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';
import "./Modal.scss";

function Modal({children, className, isOpen, onClose}) {
    return (
        <MUIModal
            open={isOpen}
            onClose={onClose}
            className={`modal-overlay ${className}`}
            closeAfterTransition
            slotProps={{
                backdrop: {
                    timeout: 500,
                },
            }}
        >
            <Box className={`modal-content ${className}`} onClick={(e) => e.stopPropagation()}>
                <IconButton className="modal-close" onClick={onClose} size="large">
                    <CloseIcon/>
                </IconButton>
                {children}
            </Box>
        </MUIModal>
    );
}

export default Modal;