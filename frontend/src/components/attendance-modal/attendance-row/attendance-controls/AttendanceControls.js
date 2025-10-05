import {ToggleButton, ToggleButtonGroup, Tooltip} from "@mui/material";
import CheckBoxIcon from '@mui/icons-material/CheckBox';
import DisabledByDefaultIcon from '@mui/icons-material/DisabledByDefault';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import CreateIcon from '@mui/icons-material/Create';
import {attendanceTypeMap, absentValue, presentValue, lateValue, excusedValue} from "./attendanceTypeMap";
import "./AttendanceControls.scss"


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
        <Tooltip title={attendanceTypeMap.presentValue}>
            <ToggleButton value={presentValue} className="present">
                <CheckBoxIcon/>
            </ToggleButton>
        </Tooltip>
        <Tooltip title={attendanceTypeMap.absentValue}>
            <ToggleButton value={absentValue} className="absent" aria-label="absent">
                <DisabledByDefaultIcon/>
            </ToggleButton>
        </Tooltip>
        <Tooltip title={attendanceTypeMap.lateValue}>
            <ToggleButton value={lateValue} className="late" aria-label="late">
                <AccessTimeIcon/>
            </ToggleButton>
        </Tooltip>
        <Tooltip title={attendanceTypeMap.excusedValue}>
            <ToggleButton value={excusedValue} className="excused" aria-label="absent">
                <CreateIcon/>
            </ToggleButton>
        </Tooltip>
    </ToggleButtonGroup>
}

export default AttendanceControls