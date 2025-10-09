import { Box, IconButton } from "@mui/material";
import DragIndicatorIcon from "@mui/icons-material/DragIndicator";
import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities"; // ✅ add this
import "./SortableItem.scss"

const SortableItem = ({ id, children }) => {
    const {
        setNodeRef,
        setActivatorNodeRef,
        attributes,
        listeners,
        transform,
        transition,
        isDragging,
    } = useSortable({ id });

    const style = {
        transform: CSS.Transform.toString(transform),
        transition,
        opacity: isDragging ? 0.4 : 1
    };

    return (
        <div ref={setNodeRef} style={style}>
            <Box
                className={`sortable-item-box ${isDragging ? "dragging" : ""}`}
            >
                <IconButton
                    className="icon-button"
                    ref={setActivatorNodeRef}
                    {...listeners}
                    {...attributes}
                    size="small"
                >
                    <DragIndicatorIcon />
                </IconButton>
                <Box className="child-box">{children}</Box>
            </Box>

        </div>
    );
};

export default SortableItem;
