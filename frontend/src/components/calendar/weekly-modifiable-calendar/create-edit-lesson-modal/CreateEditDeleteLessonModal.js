import Modal from "../../../modal/Modal";
import {Autocomplete, Box, Button, TextField, Typography} from "@mui/material";
import options from "../../../manual-calendar/group-teacher-chooser/GroupTeacherOptions";
import {useEffect, useState} from "react";

const CreateEditDeleteLessonModal = ({
                                         event,
                                         groupData,
                                         perspective,
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

    const setPredefinedFields = () => {
        if (!event) return
        console.log(event)
        setChosenGroup(groupData.find(group => group.id === event.groupId))
        setChosenTeacher(teacherData.find(teacher => teacher.id === event.teacherId))
        setChosenSubject(subjectData.find(subject => subject.id === event.subjectId))
        setChosenRoom(roomData.find(room => room.room_code === event.room))
    }

    const clearFields = () => {
        setChosenGroup(null)
        setChosenTeacher(null)
        setChosenSubject(null)
        setChosenRoom(null)
    }

    const handleEdit = () => {
        const modifiedEvent = {
            ...event,
            title: chosenSubject.name,
            groupId: chosenGroup.id,
            groupCode: chosenGroup.group_code,
            teacherId: chosenTeacher.id,
            teacher: chosenTeacher.name + " " + chosenTeacher.surname,
            subjectId: chosenSubject.id,
            subject: chosenSubject.name,
            roomId: chosenRoom.id,
            room: chosenRoom.room_code,
            cancelled: false,
            modified: false
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
                disabled={!isCreated && (!!perspective && perspective === options.GroupPerspective)}
            />
            <Autocomplete
                className="teacher-chooser"
                options={teacherData}
                getOptionLabel={teacher => teacher.name + " " + teacher.surname}
                value={chosenTeacher}
                onChange={(event, value) => {
                    setChosenTeacher(value)
                }}
                renderInput={(params) => (
                    <TextField {...params} label="Nauczyciel"/>
                )}
                disabled={!isCreated && (!!perspective && perspective === options.TeacherPerspective)}
            />
            <Autocomplete
                className="subject-chooser"
                options={subjectData}
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
            <Button className="button-edit" variant="contained" onClick={handleEdit}
                    disabled={!chosenGroup || !chosenTeacher || !chosenSubject || !chosenRoom}>
                {isCreated ? "Stwórz" : "Edytuj"}
            </Button>
            {isCreated ? null :
                <Button className="button-delete" variant="contained" onClick={handleDelete}>
                    Odwołaj
                </Button>
            }
        </Box>
    </Modal>
}

export default CreateEditDeleteLessonModal