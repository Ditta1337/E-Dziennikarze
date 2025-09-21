import {ToggleButton, ToggleButtonGroup, Tooltip} from "@mui/material";
import CheckBoxIcon from '@mui/icons-material/CheckBox';
import DisabledByDefaultIcon from '@mui/icons-material/DisabledByDefault';
import "./AttendanceControls.scss"

const presentValue = "PRESENT"
const absentValue = "ABSENT"

const AttendanceControls = ({value, onChange}) => {

    const handleChange = (e, newValue) => {
        if (newValue !== null) {
            onChange(newValue)
        }
    }

    return <ToggleButtonGroup value={value}
                              exclusive
                              onChange={handleChange}
                              size="small"
                              className="attendance-controls">
        <Tooltip title="Present">
            <ToggleButton value={presentValue} className="present">
                <CheckBoxIcon/>
            </ToggleButton>
        </Tooltip>
        <Tooltip title="Absent">
            <ToggleButton value={absentValue} className="absent" aria-label="absent">
                <DisabledByDefaultIcon/>
            </ToggleButton>
        </Tooltip>
    </ToggleButtonGroup>
}

export default AttendanceControls