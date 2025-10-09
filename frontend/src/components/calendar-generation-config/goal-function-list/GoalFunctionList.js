import React, {useEffect, useState} from "react"
import {
    DndContext,
    DragOverlay,
    PointerSensor,
    useSensor,
    useSensors,
    rectIntersection,
} from "@dnd-kit/core"
import { SortableContext, arrayMove, rectSortingStrategy } from "@dnd-kit/sortable"
import { Box, IconButton, Typography } from "@mui/material"
import DragIndicatorIcon from "@mui/icons-material/DragIndicator"
import DraggableGoalFunction from "../draggable-goal-function/DraggableGoalFunction"
import SortableItem from "../../DndSortableItem/SortableItem"
import DroppableContainer from "./droppable-container/DroppableContainer"
import "./GoalFunctionList.scss"

const GoalFunctionList = ({ goalFunctions, constructConfigData }) => {
    const [containers, setContainers] = useState({
        ACTIVE: goalFunctions || [],
        INACTIVE: [],
    })
    const [functionDuration, setFunctionDuration] = useState( Object.fromEntries((goalFunctions || []).map(goal => [goal.function_name, 0])) )
    const [activeId, setActiveId] = useState(null)

    const sensors = useSensors(useSensor(PointerSensor))

    const findContainer = (id) => {
        if (!id) return null
        return Object.keys(containers).find((key) =>
            containers[key].some((goal) => goal.function_name === id)
        )
    }

    const getActiveGoal = () => {
        if (!activeId) return null
        return Object.values(containers)
            .flat()
            .find((goal) => goal.function_name === activeId)
    }

    const handleDurationChange = (functionName, durationInSeconds) => {
        setFunctionDuration(prev => {
            const updated = {
                ...prev,
                [functionName]: durationInSeconds,
            }
            constructConfigData(containers.ACTIVE, updated) // ✅ use the updated object
            return updated
        })
    }

    const handleDragStart = (event) => {
        setActiveId(event.active.id)
    }

    const handleDragEnd = (event) => {
        const { active, over } = event
        setActiveId(null)
        if (!over) return

        const activeContainer = findContainer(active.id)
        const overContainer = findContainer(over.id) || over.id

        if (!activeContainer || !overContainer) return

        if (activeContainer === overContainer) {
            setContainers((prev) => {
                const items = [...prev[activeContainer]]
                const oldIndex = items.findIndex((g) => g.function_name === active.id)
                const newIndex = items.findIndex((g) => g.function_name === over.id)
                return {
                    ...prev,
                    [activeContainer]: arrayMove(items, oldIndex, newIndex),
                }
            })
        } else {
            setContainers((prev) => {
                const activeItems = [...prev[activeContainer]]
                const overItems = [...prev[overContainer]]
                const oldIndex = activeItems.findIndex((g) => g.function_name === active.id)
                const [movedItem] = activeItems.splice(oldIndex, 1)
                overItems.push(movedItem)
                return {
                    ...prev,
                    [activeContainer]: activeItems,
                    [overContainer]: overItems,
                }
            })
        }
    }

    const activeGoal = getActiveGoal()

    useEffect(() => {
        constructConfigData(containers.ACTIVE, functionDuration)
    }, [containers, goalFunctions])

    return (
        <DndContext
            sensors={sensors}
            collisionDetection={rectIntersection}
            onDragStart={handleDragStart}
            onDragEnd={handleDragEnd}
        >
            <Box className="goal-function-list">
                <Typography className="goals-comment">
                    Przesuń obiekty aby zmienić priorytet funkcji.
                </Typography>

                <DroppableContainer
                    id="ACTIVE"
                    items={containers.ACTIVE.map((g) => g.function_name)}
                    label="Aktywne Cele"
                >
                    <SortableContext
                        items={containers.ACTIVE.map((g) => g.function_name)}
                        strategy={rectSortingStrategy}
                    >
                        <Box className="sortable-items">
                            {containers.ACTIVE.map((goal) => (
                                <SortableItem key={goal.function_name} id={goal.function_name}>
                                    <DraggableGoalFunction functionData={goal} onDurationChange={handleDurationChange} isActive={true}/>
                                </SortableItem>
                            ))}
                        </Box>
                    </SortableContext>
                </DroppableContainer>

                <DroppableContainer
                    id="INACTIVE"
                    items={containers.INACTIVE.map((g) => g.function_name)}
                    label="Nieaktywne Cele"
                >
                    <SortableContext
                        items={containers.INACTIVE.map((g) => g.function_name)}
                        strategy={rectSortingStrategy}
                    >
                        <Box className="sortable-items">
                            {containers.INACTIVE.map((goal) => (
                                <SortableItem key={goal.function_name} id={goal.function_name}>
                                    <DraggableGoalFunction functionData={goal} isActive={false}/>
                                </SortableItem>
                            ))}
                        </Box>
                    </SortableContext>
                </DroppableContainer>
            </Box>

            <DragOverlay>
                {activeGoal && (
                    <Box className="drag-overlay">
                        <IconButton size="small" className="drag-overlay-handle">
                            <DragIndicatorIcon />
                        </IconButton>
                        <Box className="drag-overlay-content">
                            <DraggableGoalFunction functionData={activeGoal} onDurationChange={handleDurationChange}/>
                        </Box>
                    </Box>
                )}
            </DragOverlay>
        </DndContext>
    )
}

export default GoalFunctionList
