import React, {useState} from "react";
import {Avatar, Box, ListItem, ListItemAvatar, ListItemText, Typography} from "@mui/material";
import AttendanceControls from "./attendance-controls/AttendanceControls";
import "./AttendanceRow.scss"
import {post, put} from "../../../api";

const AttendanceRow = ({studentData, lessonId, subjectId, existingAttendanceData}) => {
    const [attendance, setAttendance] = useState(existingAttendanceData)

    const submitAttendance = async (attendance) => {
        try {
            const newAttendanceData = await post("/attendance", attendance)
            setAttendance(newAttendanceData.data)
        } catch (error) {
            console.error("Submit attendance error:", error);
        }
    }

    const updateAttendance = async (attendance) => {
        try {
            const updatedAttendanceData = await put(`/attendance`, attendance)
            setAttendance(updatedAttendanceData.data)
        } catch (error) {
        }
    }

    const handleStatusChange = async (newStatus) => {
        const attendanceChanges = {
            lesson_id: lessonId,
            student_id: studentData.id,
            subject_id: subjectId,
            status: newStatus
        };

        if (attendance === null) {
            submitAttendance(attendanceChanges);
        } else {
            const updatePayload = {
                ...attendance,
                ...attendanceChanges
            };
            updateAttendance(updatePayload);
        }
    };

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
            <AttendanceControls value={attendance ? attendance.status : null} onChange={handleStatusChange}/>
        </Box>
    </ListItem>
}

export default AttendanceRow