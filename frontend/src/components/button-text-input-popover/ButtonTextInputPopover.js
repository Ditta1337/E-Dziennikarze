import {Button, Popover, TextField} from "@mui/material";
import React, {useState} from "react";

const ButtonTextInputPopover = ({
                                    popoverOpenButtonText,
                                    popoverTextFieldLabel,
                                    popoverInnerButtonText,
                                    handlePopoverButtonClick
                                }) => {
    const [anchorEl, setAnchorEl] = useState(null)
    const [textInput, setTextInput] = useState(null)

    const copyConfigurationPopoverOpen = Boolean(anchorEl)
    const id = copyConfigurationPopoverOpen ? "function-info-popover" : undefined

    const handleOpen = (event) => {
        setAnchorEl(event.currentTarget)
    }

    const handleClose = () => {
        setAnchorEl(null)
    }

    return <>
        <Button variant="contained" size="medium" onClick={handleOpen}>
            {popoverOpenButtonText}
        </Button>
        <Popover
            id={id}
            open={copyConfigurationPopoverOpen}
            anchorEl={anchorEl}
            onClose={handleClose}
            anchorOrigin={{
                vertical: "bottom",
                horizontal: "center",
            }}
            transformOrigin={{
                vertical: "top",
                horizontal: "center",
            }}
            PaperProps={{
                style: {
                    padding: "0.75rem",
                    maxWidth: 250,
                    backgroundColor: "#f9fafc",
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center"

                },
            }}
        >
            <TextField
                // className="configuration-name-input"
                label={popoverTextFieldLabel}
                variant="outlined"
                value={textInput}
                onChange={(e) => {
                    setTextInput(e.target.value)
                }}
            />
            <Button
                // className="configuration-name-button"
                variant="contained"
                size="small"
                onClick={() => handlePopoverButtonClick(textInput)}
            >
                {popoverInnerButtonText}
            </Button>
        </Popover>
    </>
}

export default ButtonTextInputPopover