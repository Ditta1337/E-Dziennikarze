import React, {useEffect, useState} from "react"
import {DndContext, DragOverlay, PointerSensor, rectIntersection, useSensor, useSensors,} from "@dnd-kit/core"
import {arrayMove, rectSortingStrategy, SortableContext} from "@dnd-kit/sortable"
import {Box, CircularProgress, IconButton, Typography} from "@mui/material"
import DragIndicatorIcon from "@mui/icons-material/DragIndicator"
import DraggableGoalFunction from "../draggable-goal-function/DraggableGoalFunction"
import SortableItem from "../../dnd-sortable-item/SortableItem"
import DroppableContainer from "./droppable-container/DroppableContainer"
import "./GoalFunctionList.scss"


const GoalFunctionList = ({configurationData, setConfigurationData, fetchGoalFunctions, displaySnackbarMessage}) => {
    const [goalFunctions, setGoalFunctions] = useState(null)
    const [containers, setContainers] = useState(null)
    const [functionDuration, setFunctionDuration] = useState(null)
    const [activeId, setActiveId] = useState(null)

    const sensors = useSensors(useSensor(PointerSensor))

    const loadGoalFunctions = async () => {
        try {
            const result = await fetchGoalFunctions()
            setGoalFunctions(result.data)
        } catch (e) {
            console.log(e)
            displaySnackbarMessage("Wystąpił błąd podczas wczytywania funkcji celu")
        }
    }

    const fillContainersFromConfigData = () => {
        if (!goalFunctions || !configurationData?.configuration) {
            setContainers({ ACTIVE: [], INACTIVE: [] })
            return
        }
        const configGoals = configurationData.configuration.goals
        const activeGoalNames = configGoals.map(goal => goal.name)
        const activeGoals = configGoals
            .map(cfgGoal => goalFunctions.find(gf => gf.function_name === cfgGoal.name))
            .filter(Boolean)
        const inactiveGoals = goalFunctions.filter(
            gf => !activeGoalNames.includes(gf.function_name)
        )
        const activeDurations = Object.fromEntries(
            configGoals.map(goal => [goal.name, goal.time])
        )
        const inactiveDurations = Object.fromEntries(
            inactiveGoals.map(goal => [goal.function_name, 0])
        )
        const allDurations = { ...activeDurations, ...inactiveDurations }
        setFunctionDuration(allDurations)
        setContainers({ ACTIVE: activeGoals, INACTIVE: inactiveGoals })
    }


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
            return {
                ...prev,
                [functionName]: durationInSeconds,
            }
        })
    }

    const handleDragStart = (event) => {
        setActiveId(event.active.id)
    }

    const handleDragEnd = (event) => {
        const {active, over} = event
        setActiveId(null)
        if (!over) return

        const activeContainer = findContainer(active.id)
        const overContainer = findContainer(over.id) || over.id

        if (!activeContainer || !overContainer) return

        if (activeContainer === "ACTIVE" && overContainer === "INACTIVE") {
            if (containers.ACTIVE.length <= 1) {
                return
            }
        }

        if (activeContainer === overContainer) {
            setContainers((prev) => {
                const items = [...prev[activeContainer]]
                console.log(items)
                const oldIndex = items.findIndex((g) => g.function_name === active.id)
                console.log(oldIndex)
                const newIndex = items.findIndex((g) => g.function_name === over.id)
                console.log(newIndex)
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
        loadGoalFunctions()
    }, [])

    useEffect(() => {
        fillContainersFromConfigData()
    }, [goalFunctions])

    useEffect(() => {
        if (!containers || !functionDuration) return

        const activeGoals = containers.ACTIVE.map(goal => ({
            name: goal.function_name,
            time: functionDuration[goal.function_name] || 0,
        }))

        setConfigurationData(prev => {
            const prevGoals = prev?.configuration?.goals || []
            const isEqual = prevGoals.length === activeGoals.length &&
                prevGoals.every((g, i) => g.name === activeGoals[i].name && g.time === activeGoals[i].time)

            if (isEqual) return prev
            return {
                ...prev,
                configuration: {
                    ...prev.configuration,
                    goals: activeGoals,
                }
            }
        })
    }, [containers, functionDuration, setConfigurationData])


    if (!goalFunctions || !functionDuration || !containers) {
        return <CircularProgress/>
    }

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
                                    <DraggableGoalFunction functionData={goal}
                                                           functionDuration={functionDuration[goal.function_name]}
                                                           onDurationChange={handleDurationChange}/>
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
                                    <DraggableGoalFunction functionData={goal}
                                                           functionDuration={functionDuration[goal.function_name]}
                                                           isActive={false}/>
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
                            <DragIndicatorIcon/>
                        </IconButton>
                        <Box className="drag-overlay-content">
                            <DraggableGoalFunction functionData={activeGoal}/>
                        </Box>
                    </Box>
                )}
            </DragOverlay>
        </DndContext>
    )
}

export default GoalFunctionList
