import {Avatar, Box, ListItem, ListItemAvatar, ListItemText, Typography} from "@mui/material";
import AttendanceControls from "./attendance-controls/AttendanceControls";
import JustifiedAbsence from "./justified-absence/JustifiedAbsence";
import "./AttendanceRow.scss"
import {useState} from "react";

const AttendanceRow = ({studentData, onStatusChange}) => {
    const [studentStatus, setStudentStatus] = useState(studentData.status)

    const handleStatusChange = (newStatus) => {
        setStudentStatus(newStatus)
        onStatusChange(studentData.id, newStatus)
    }

    return <ListItem className="attendance-row">
        <ListItemAvatar>
            <Avatar className="student-avatar"
                    src={studentData.image_base64}
                    alt={`${studentData.name} ${studentData.surname}`}/>
        </ListItemAvatar>
        <ListItemText
            primary={<Typography className="student-name">
                {`${studentData.name} ${studentData.surname}`}
            </Typography>}
        />
        <Box className="attendance-actions">
            <JustifiedAbsence value={studentStatus} onSelect={handleStatusChange}/>
            <AttendanceControls value={studentStatus} onChange={handleStatusChange}/>
        </Box>
    </ListItem>
}

export default AttendanceRow