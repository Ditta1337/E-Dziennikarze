import {useState} from "react"
import {Box, Typography, IconButton, Popover, Tooltip} from "@mui/material"
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined'
import DurationPicker from "../../calendar/duration-picker/DurationPicker"
import "./DraggableGoalFunction.scss"

const DraggableGoalFunction = ({functionData, onDurationChange, isActive}) => {
    const [anchorEl, setAnchorEl] = useState(null)

    const handleDurationChange = (durationInSeconds) => {
        onDurationChange(functionData.function_name, durationInSeconds)
    }

    const handleOpenInfo = (event) => {
        setAnchorEl(event.currentTarget)
    }

    const handleCloseInfo = () => {
        setAnchorEl(null)
    }

    const open = Boolean(anchorEl)
    const id = open ? "function-info-popover" : undefined

    return (
        <Box className="container" display="flex" alignItems="center" gap={1}>
            <Typography variant="body1">{functionData.name}</Typography>

            <Box className="duration-info-container">
                <Tooltip title="Show description">
                    <IconButton size="small" onClick={handleOpenInfo}>
                        <InfoOutlinedIcon fontSize="small"/>
                    </IconButton>
                </Tooltip>

                <Popover
                    id={id}
                    open={open}
                    anchorEl={anchorEl}
                    onClose={handleCloseInfo}
                    anchorOrigin={{
                        vertical: "top",
                        horizontal: "center",
                    }}
                    transformOrigin={{
                        vertical: "bottom",
                        horizontal: "center",
                    }}
                    PaperProps={{
                        style: {padding: "0.75rem", maxWidth: 250, backgroundColor: "#f9fafc"},
                    }}
                >
                    <Typography variant="body2">{functionData.description}</Typography>
                </Popover>

                <DurationPicker onChange={handleDurationChange} isActive={isActive}/>
            </Box>
        </Box>
    )
}

export default DraggableGoalFunction
