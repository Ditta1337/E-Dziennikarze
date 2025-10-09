import { useDroppable } from "@dnd-kit/core";
import { Box, Paper, Typography } from "@mui/material";
import React from "react";
import "./DroppableContainer.scss";

const DroppableContainer = ({ id, items, label, children }) => {
    const { setNodeRef, isOver } = useDroppable({ id });

    return (
        <Paper
            ref={setNodeRef}
            className={`droppable-container ${id.toLowerCase()} ${isOver ? "is-over" : ""}`}
        >
            <Typography variant="subtitle1" className="droppable-label">
                {label}
            </Typography>

            {children}

            {items.length === 0 && (
                <Box className="droppable-empty">Drop items here</Box>
            )}
        </Paper>
    );
};

export default DroppableContainer;
