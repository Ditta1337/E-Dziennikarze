import Modal from "../../modal/Modal"
import {Autocomplete, Box, Button, TextField, Typography} from "@mui/material"
import options from "../group-teacher-chooser/GroupTeacherOptions"
import {useEffect, useState} from "react"
import "./CreateEditLessonModal.scss"

const CreateEditLessonModal = ({
                                   event,
                                   perspective,
                                   groupData,
                                   teacherData,
                                   subjectData,
                                   roomData,
                                   isOpen,
                                   onClose,
                                   isCreated,
                                   saveEvent,
                                   deleteEvent
                               }) => {
    const [chosenGroup, setChosenGroup] = useState(null)
    const [chosenTeacher, setChosenTeacher] = useState(null)
    const [chosenSubject, setChosenSubject] = useState(null)
    const [chosenRoom, setChosenRoom] = useState(null)
    const [filteredTeachers, setFilteredTeachers] = useState(teacherData)
    const [filteredSubjects, setFilteredSubjects] = useState(subjectData)

    const setPredefinedFields = () => {
        if (!perspective) return
        if (perspective.chosenPerspective === options.GroupPerspective) {
            setChosenGroup(perspective)
            if (!event) return
            setChosenTeacher(teacherData.find(teacher => teacher.id === event.teacherId))
            setChosenSubject(subjectData.find(subject => subject.id === event.subjectId))
            setChosenRoom(roomData.find(room => room.id === event.roomId))
        }
        if (perspective.chosenPerspective === options.TeacherPerspective) {
            setChosenTeacher(perspective)
            if (!event) return
            setChosenGroup(groupData.find(group => group.id === event.groupId))
            setChosenSubject(subjectData.find(subject => subject.id === event.subjectId))
            setChosenRoom(roomData.find(room => room.id === event.roomId))
        }
    }

    const constraintTeachersSubjects = () => {
        if (!teacherData || !subjectData) return
        if (!!chosenTeacher && !!chosenSubject || !chosenTeacher && !chosenSubject) {
            setFilteredTeachers(teacherData)
            setFilteredSubjects(subjectData)
        }
        if (chosenTeacher) {
            const teacherSubjectIds = chosenTeacher.subjects_taught.map(subject => subject.subject_id)
            setFilteredSubjects(subjectData.filter(subject => teacherSubjectIds.includes(subject.id)))
        }
        if (chosenSubject) {
            setFilteredTeachers(teacherData.filter(teacher => teacher.subjects_taught.some(subject => subject.subject_id === chosenSubject.id)))
        }
    }

    const clearFields = () => {
        if (perspective.chosenPerspective === options.TeacherPerspective) {
            setChosenGroup(null)
            setChosenSubject(null)
            setChosenRoom(null)
        }
        if (perspective.chosenPerspective === options.GroupPerspective) {
            setChosenTeacher(null)
            setChosenSubject(null)
            setChosenRoom(null)
        }
    }

    const handleEdit = () => {
        const modifiedEvent = {
            ...event,
            groupId: chosenGroup.id,
            groupCode: chosenGroup.group_code,
            teacherId: chosenTeacher.id,
            teacher: chosenTeacher.name + " " + chosenTeacher.surname,
            subjectId: chosenSubject.id,
            subject: chosenSubject.name,
            roomId: chosenRoom.id,
            room: chosenRoom.room_code,
        }
        saveEvent(modifiedEvent)
        handleClose()
    }

    const handleDelete = () => {
        deleteEvent(event)
        handleClose()
    }

    const handleClose = () => {
        clearFields()
        onClose()
    }

    useEffect(() => {
        setPredefinedFields()
    }, [event])

    useEffect(() => {
        constraintTeachersSubjects()
    }, [chosenTeacher, chosenSubject, teacherData, subjectData]);

    useEffect(() => {
        console.log(event)
    }, [event]);

    if (!event) return null

    return <Modal
        className="create-edit-lesson-modal"
        isOpen={isOpen}
        onClose={handleClose}
    >
        <Typography variant="h6" className="editor-title">
            {isCreated ? "Stwórz lekcję" : "Edytuj lekcję"}
        </Typography>
        <Box className="lesson-edit">
            <Autocomplete
                className="group-chooser"
                options={groupData}
                getOptionLabel={(group) => group.group_code}
                value={chosenGroup}
                onChange={(event, value) => {
                    setChosenGroup(value)
                }}
                renderInput={(params) => (
                    <TextField {...params} label="Grupa"/>
                )}
                disabled={!!perspective && perspective.chosenPerspective === options.GroupPerspective}
            />
            <Autocomplete
                className="teacher-chooser"
                options={filteredTeachers}
                getOptionLabel={teacher => teacher.name + " " + teacher.surname}
                value={chosenTeacher}
                onChange={(event, value) => {
                    setChosenTeacher(value)
                }}
                renderInput={(params) => (
                    <TextField {...params} label="Nauczyciel"/>
                )}
                disabled={!!perspective && perspective.chosenPerspective === options.TeacherPerspective}
            />
            <Autocomplete
                className="subject-chooser"
                options={filteredSubjects}
                getOptionLabel={subject => subject.name}
                value={chosenSubject}
                onChange={(event, value) => {
                    setChosenSubject(value)
                }}
                renderInput={(params) => (
                    <TextField {...params} label="Przedmiot"/>
                )}
            />
            <Autocomplete
                className="room-chooser"
                options={roomData}
                getOptionLabel={room => room.room_code}
                value={chosenRoom}
                onChange={(event, value) => {
                    setChosenRoom(value)
                }}
                renderInput={(params) => (
                    <TextField {...params} label="Pokój"/>
                )}
            />
        </Box>
        <Box className="buttons">
            <Button variant="outlined" color="inherit" onClick={handleClose}>
                Anuluj
            </Button>
            <Button className="button-edit" variant="contained" onClick={handleEdit} disabled={!chosenGroup || !chosenTeacher || !chosenSubject || !chosenRoom}>
                {isCreated ? "Stwórz" : "Edytuj"}
            </Button>
            {isCreated ? null :
                <Button className="button-delete" variant="contained" onClick={handleDelete}>
                    Usuń
                </Button>
            }
        </Box>
    </Modal>

}

export default CreateEditLessonModal