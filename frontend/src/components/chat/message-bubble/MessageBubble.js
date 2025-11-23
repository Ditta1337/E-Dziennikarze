import React from "react";
import {
    Box,
    Paper,
    Typography,
    IconButton,
    ListItem,
    Tooltip,
} from "@mui/material";
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import "./MessageBubble.scss";

const MessageBubble = ({ message, currentUserId, onEdit, onDelete }) => {
    const isSentByUser = message.senderId === currentUserId;
    const isDeleted = message.status === "DELETED";
    const isEdited = message.status === "EDITED";
    const isFile = message.type === "FILE";

    let content;

    if (isDeleted) {
        content = (
            <Typography variant="body2" className="text-deleted">
                Wiadomość została usunięta
            </Typography>
        );
    } else if (isFile) {
        content = (
            <a
                href={`http://localhost:8443/message/file/${message.filePath}`}
                target="_blank"
                rel="noopener noreferrer"
                className="file-link"
            >
                {message.content}
            </a>
        );
    } else {
        content = (
            <Typography variant="body1" className="text-content">
                {message.content}
            </Typography>
        );
    }

    return (
        <ListItem className={`message-item ${isSentByUser ? 'sent' : 'received'}`}>
            <Box className="message-wrapper">
                <Paper elevation={1} className="message-bubble">
                    {content}

                    {isEdited && !isDeleted && (
                        <Typography variant="caption" className="edited-label">
                            (edytowano)
                        </Typography>
                    )}
                </Paper>

                {isSentByUser && !isDeleted && (
                    <Box className="message-actions">
                        <Tooltip title="Edytuj">
                            <IconButton size="small" onClick={() => onEdit(message)}>
                                <EditIcon fontSize="small" />
                            </IconButton>
                        </Tooltip>
                        <Tooltip title="Usuń">
                            <IconButton size="small" onClick={() => onDelete(message)}>
                                <DeleteIcon fontSize="small" />
                            </IconButton>
                        </Tooltip>
                    </Box>
                )}
            </Box>
        </ListItem>
    );
};

export default MessageBubble;