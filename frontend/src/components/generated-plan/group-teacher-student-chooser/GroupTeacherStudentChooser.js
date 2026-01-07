import {Autocomplete, Box, TextField} from "@mui/material"
import "./GroupTeacherStudentChooser.scss"
import options from "./GroupTeacherStudentOptions"
import {useEffect, useState} from "react"

const createTeacherWithGroups = (teachers, groupSubjectData) => {
    const uniqueTeachers = Array.from(new Map(
        teachers.map(t => [t.id, t])
    ).values())

    return uniqueTeachers.map(teacher => {
        const groupIds = groupSubjectData
            .filter(gs => gs.teacher_id === teacher.id)
            .map(gs => gs.group_id)

        const uniqueGroupIds = [...new Set(groupIds)]

        return {
            teacher_id: teacher.id,
            name: `${teacher.name} ${teacher.surname}`,
            groups: uniqueGroupIds
        }
    })
}

const createGroupIdToGroupCode = (groups, groupSubjectData) => {
    const groupMap = Object.fromEntries(groups.map(g => [g.id, g.group_code]))
    return groupSubjectData
        .map(gs => gs.group_id)
        .filter((id, i, arr) => arr.indexOf(id) === i)
        .map(id => ({
            group_id: id,
            group_code: groupMap[id] || ""
        }))
}

const createStudentIdToName = (students) => {
    return students.map(student => ({
        id: student.id,
        name: `${student.name} ${student.surname}`
    }))
}

const mapStudentsGroupsToIdArray = (groups) => {
    return groups.map(group => (group.id))
}

const GroupTeacherStudentChooser = ({
                                        fetchGroupSubject,
                                        fetchStudents,
                                        fetchGroups,
                                        fetchTeachers,
                                        fetchStudentsGroups,
                                        setGroupsToDisplay,
                                        setTeacherToDisplay,
                                        setStudentToDisplay = () => {},
                                        disabled = false
                                    }) => {
    const [chosenPerspective, setChosenPerspective] = useState(null)
    const [chosenEntity, setChosenEntity] = useState(null)
    const [groupData, setGroupData] = useState([])
    const [teacherData, setTeacherData] = useState([])
    const [studentData, setStudentData] = useState([])

    const updateGroupTeacherData = async () => {
        try {
            const teacherResult = await fetchTeachers()
            const groupResult = await fetchGroups()
            const groupSubjectResult = await fetchGroupSubject()
            setTeacherData(createTeacherWithGroups(teacherResult.data, groupSubjectResult.data))
            setGroupData(createGroupIdToGroupCode(groupResult.data, groupSubjectResult.data))
        } catch (e) {
            console.log(e)
        }
    }

    const updateStudentData = async () => {
        try {
            const result = await fetchStudents()
            setStudentData(createStudentIdToName(result.data))
        } catch (e) {
            console.log(e)
        }
    }

    const getStudentGroups = async (id) => {
        try {
            const result = await fetchStudentsGroups(id)
            return mapStudentsGroupsToIdArray(result.data)
        } catch (e) {
            console.log(e)
        }
    }

    const getSpecificEntityOptions = () => {
        if (chosenPerspective === options.GroupPerspective) {
            return groupData
        } else if (chosenPerspective === options.TeacherPerspective) {
            return teacherData
        } else if (chosenPerspective === options.StudentPerspective) {
            return studentData
        }
        return []
    }

    const getSpecificEntityOptionLabel = (option) => {
        if (!option) return ""
        if (chosenPerspective === options.GroupPerspective) {
            return option.group_code || ""
        } else if (chosenPerspective === options.TeacherPerspective) {
            return option.name || ""
        } else if (chosenPerspective === options.StudentPerspective) {
            return option.name || ""
        }
        return ""
    }

    const handleChosenPerspectiveChange = (event, value) => {
        setChosenPerspective(value)
        setChosenEntity(null)
        setGroupsToDisplay([])
    }

    const handleSpecificEntityChange = async (event, value) => {
        setChosenEntity(value)
        if (value === null) {
            setGroupsToDisplay([])
            return
        }
        if (chosenPerspective === options.GroupPerspective) {
            setTeacherToDisplay(null)
            setStudentToDisplay(null)
            setGroupsToDisplay([value.group_id])
        } else if (chosenPerspective === options.TeacherPerspective) {
            setGroupsToDisplay([])
            setTeacherToDisplay(value.teacher_id)
        } else if (chosenPerspective === options.StudentPerspective) {
            const studentGroups = await getStudentGroups(value.id)
            setTeacherToDisplay(null)
            setGroupsToDisplay(studentGroups)
            setStudentToDisplay(value.id)
        } else {
            setGroupsToDisplay([])
        }
    }

    useEffect(() => {
        updateGroupTeacherData()
        updateStudentData()
    }, [])

    return <Box className="group-teacher-student-chooser">
        <Autocomplete
            className="group-teacher-student"
            options={Object.values(options)}
            value={chosenPerspective}
            onChange={handleChosenPerspectiveChange}
            renderInput={(params) => (
                <TextField
                    {...params}
                    label="Perspektywa"
                />
            )}
            disabled={disabled}
        />
        <Autocomplete
            className="specific-entity"
            options={getSpecificEntityOptions()}
            getOptionLabel={getSpecificEntityOptionLabel}
            value={chosenEntity}
            onChange={handleSpecificEntityChange}
            renderInput={(params) => (
                <TextField {...params} label="Wybierz konkretną instancję"/>
            )}
            disabled={disabled || !chosenPerspective}
        />
    </Box>
}

export default GroupTeacherStudentChooser