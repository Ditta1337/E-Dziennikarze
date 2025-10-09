import {useCallback, useEffect, useMemo, useState} from "react"
import {Autocomplete, Box, Button, Chip, TextField} from "@mui/material"
import {Formik, Form, Field} from "formik"
import * as Yup from "yup"
import "./GroupSubjectForm.scss"
import {LessonAnyType, lessonPlacementTypesTopPolish} from "../lesson-placement-type/LessonPlacementType";

const GroupSubjectForm = ({fetchGroupSubjectData, fetchRooms}) => {
    const [groupSubjectData, setGroupSubjectData] = useState([])
    const [rooms, setRooms] = useState([])

    const updateGroupSubjectData = useCallback(async () => {
        try {
            const response = await fetchGroupSubjectData()
            console.log(response.data)
            setGroupSubjectData(response?.data || [])
        } catch (e) {
            console.error("Failed to fetch group subjects:", e)
        }
    }, [fetchGroupSubjectData])

    const updateRooms = useCallback(async () => {
        try {
            const response = await fetchRooms()
            setRooms(response?.data || [])
        } catch (e) {
            console.error("Failed to fetch rooms:", e)
        }
    }, [fetchRooms])

    useEffect(() => {
        updateGroupSubjectData()
        updateRooms()
    }, [updateGroupSubjectData, updateRooms])

    const groupsToChoose = useMemo(() => {
        const map = new Map()
        for (const gs of groupSubjectData) {
            if (!map.has(gs.group_code)) {
                map.set(gs.group_code, {
                    group_code: gs.group_code,
                    group_id: gs.group_id ?? gs.id,
                })
            }
        }
        return Array.from(map.values())
    }, [groupSubjectData])

    const subjectsForGroup = useCallback(
        (group) => {
            if (!group?.group_code) return []
            const map = new Map()
            for (const gs of groupSubjectData) {
                if (gs.group_code === group.group_code) {
                    map.set(gs.subject_id, {
                        id: gs.subject_id,
                        name: gs.subject_name,
                    })
                }
            }
            return Array.from(map.values())
        },
        [groupSubjectData]
    )

    const validationSchema = Yup.object({
        group: Yup.object().required("Wybierz grupę"),
        subject: Yup.object().required("Wybierz przedmiot"),
        rooms: Yup.array().min(1, "Wybierz pokoje"),
        lessonsPerWeek: Yup.number()
            .min(1)
            .required("Podaj liczbę lekcji w tygodniu"),
        maxLessonsPerDay: Yup.number()
            .min(1)
            .required("Podaj maksymalną liczbę lekcji w dniu"),
    })

    return (
        <Formik
            initialValues={{
                group: null,
                subject: "",
                rooms: [],
                preferredRooms: [],
                unpreferredRooms: [],
                lessonsPerWeek: "",
                maxLessonsPerDay: "",
                lessonPlacementType: ""
            }}
            validationSchema={validationSchema}
            onSubmit={(values, {setTouched, validateForm}) => {
                validateForm(values).then((errors) => {
                    if (Object.keys(errors).length) {
                        setTouched({rooms: true})
                        return
                    }
                })
                const payload = {
                    group: values.group?.group_id || values.group?.id || null,
                    subject: values.subject?.id || null,
                    rooms: values.rooms.map((r) => r.id),
                    preferredRooms: values.preferredRooms.map((r) => r.id),
                    unpreferredRooms: values.unpreferredRooms.map((r) => r.id),
                    lessonsPerWeek: values.lessonsPerWeek,
                    maxLessonsPerDay: values.maxLessonsPerDay,
                    lessonPlacementType: values.lessonPlacementType.value
                }

                console.log("Payload to submit:", payload)
            }}
        >
            {({
                  values,
                  setFieldValue,
                  errors,
                  touched,
                  handleSubmit,
                  resetForm,
                  setFieldTouched
              }) => {
                const availableSubjects = subjectsForGroup(values.group)

                return (
                    <Form onSubmit={handleSubmit} className="group-subject-form">
                        <Box className="group-subject">
                            <Autocomplete
                                className="group"
                                options={groupsToChoose}
                                getOptionLabel={(option) => option.group_code || ""}
                                value={values.group}
                                onChange={(e, newValue) => {
                                    setFieldValue("group", newValue)
                                    setFieldValue("subject", "")
                                    setFieldValue("rooms", [])
                                    setFieldValue("preferredRooms", [])
                                    setFieldValue("unpreferredRooms", [])
                                    setFieldValue("lessonPlacementType", "")
                                }}
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        label="Grupa"
                                        error={touched.group && !!errors.group}
                                        helperText={touched.group && errors.group}
                                    />
                                )}
                            />

                            <Autocomplete
                                className="subject"
                                options={availableSubjects}
                                getOptionLabel={(option) => option.name || ""}
                                value={values.subject}
                                onChange={(e, newValue) => {
                                    setFieldValue("subject", newValue || null)
                                    setFieldValue("rooms", [])
                                    setFieldValue("preferredRooms", [])
                                    setFieldValue("unpreferredRooms", [])
                                    if (newValue) {
                                        setFieldValue(
                                            "lessonPlacementType",
                                            lessonPlacementTypesTopPolish.find(type => type.value === LessonAnyType)
                                        );
                                    } else {
                                        setFieldValue("lessonPlacementType", "");
                                    }

                                }}
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        label="Przedmiot"
                                        error={touched.subject && !!errors.subject}
                                        helperText={touched.subject && errors.subject}
                                    />
                                )}
                                disabled={!values.group}
                            />
                        </Box>

                        <Autocomplete
                            className="possible-rooms"
                            multiple
                            options={rooms}
                            getOptionLabel={(option) => option.room_code || ""}
                            value={values.rooms}
                            onChange={(event, newValue) => {
                                setFieldValue("rooms", newValue || [])
                                setFieldValue(
                                    "preferredRooms",
                                    (values.preferredRooms || []).filter((r) =>
                                        (newValue || []).some((nr) => nr.id === r.id)
                                    )
                                )
                                setFieldValue(
                                    "unpreferredRooms",
                                    (values.unpreferredRooms || []).filter((r) =>
                                        (newValue || []).some((nr) => nr.id === r.id)
                                    )
                                )
                            }}
                            onBlur={() => {
                                setFieldTouched("rooms", true, true)
                            }}
                            renderTags={(value, getTagProps) =>
                                value.map((room, index) => (
                                    <Chip label={room.room_code} {...getTagProps({index})} key={room.id}/>
                                ))
                            }
                            renderInput={(params) => (
                                <TextField
                                    {...params}
                                    label="Możliwe pokoje"
                                    error={Boolean(touched.rooms && errors.rooms)}
                                    helperText={touched.rooms && errors.rooms ? errors.rooms : ""}
                                />
                            )}
                            disabled={!values.subject}
                        />

                        <Box className="prefered-not-prefered-rooms">
                            {/* Preferred Rooms */}
                            <Autocomplete
                                multiple
                                className="preferred-rooms"
                                options={values.rooms.filter(
                                    (room) =>
                                        !values.preferredRooms.some((r) => r.id === room.id) &&
                                        !values.unpreferredRooms.some((r) => r.id === room.id)
                                )}
                                getOptionLabel={(option) => option.room_code || ""}
                                value={values.preferredRooms}
                                onChange={(e, newValue) => {
                                    setFieldValue(
                                        "preferredRooms",
                                        newValue || []
                                    )
                                    setFieldValue(
                                        "unpreferredRooms",
                                        values.unpreferredRooms.filter(
                                            (room) =>
                                                !(newValue || []).some((r) => r.id === room.id)
                                        )
                                    )
                                }}
                                renderTags={(value, getTagProps) =>
                                    value.map((room, index) => (
                                        <Chip
                                            label={room.room_code}
                                            {...getTagProps({index})}
                                            key={room.id}
                                        />
                                    ))
                                }
                                renderInput={(params) => (
                                    <TextField {...params} label="Preferowane pokoje"/>
                                )}
                                disabled={values.rooms.length < 1}
                            />

                            {/* Unpreferred Rooms */}
                            <Autocomplete
                                multiple
                                className="not-preferred-rooms"
                                options={values.rooms.filter(
                                    (room) =>
                                        !values.preferredRooms.some((r) => r.id === room.id) &&
                                        !values.unpreferredRooms.some((r) => r.id === room.id)
                                )}
                                getOptionLabel={(option) => option.room_code || ""}
                                value={values.unpreferredRooms}
                                onChange={(e, newValue) => {
                                    setFieldValue(
                                        "unpreferredRooms",
                                        newValue || []
                                    )
                                    setFieldValue(
                                        "preferredRooms",
                                        values.preferredRooms.filter(
                                            (room) =>
                                                !(newValue || []).some((r) => r.id === room.id)
                                        )
                                    )
                                }}
                                renderTags={(value, getTagProps) =>
                                    value.map((room, index) => (
                                        <Chip
                                            label={room.room_code}
                                            {...getTagProps({index})}
                                            key={room.id}
                                        />
                                    ))
                                }
                                renderInput={(params) => (
                                    <TextField {...params} label="Nie wskazane pokoje"/>
                                )}
                                disabled={values.rooms.length < 1}
                            />
                        </Box>

                        {/* Numeric Inputs */}
                        <Box className="lessons-number-input">
                            <TextField
                                className="lessons-per-week"
                                type="number"
                                label="Ilość lekcji w tygodniu"
                                variant="outlined"
                                name="lessonsPerWeek"
                                value={values.lessonsPerWeek}
                                onChange={(e) =>
                                    setFieldValue("lessonsPerWeek", Number(e.target.value))
                                }
                                InputProps={{inputProps: {min: 0}}}
                                error={touched.lessonsPerWeek && !!errors.lessonsPerWeek}
                                helperText={touched.lessonsPerWeek && errors.lessonsPerWeek}
                                disabled={!values.subject}
                            />

                            <TextField
                                className="max-lessons-per-day"
                                type="number"
                                label="Maksymalna ilość lekcji w dniu"
                                variant="outlined"
                                name="maxLessonsPerDay"
                                value={values.maxLessonsPerDay}
                                onChange={(e) =>
                                    setFieldValue("maxLessonsPerDay", Number(e.target.value))
                                }
                                InputProps={{inputProps: {min: 0}}}
                                error={touched.maxLessonsPerDay && !!errors.maxLessonsPerDay}
                                helperText={touched.maxLessonsPerDay && errors.maxLessonsPerDay}
                                disabled={!values.subject}
                            />
                        </Box>
                        
                        <Autocomplete
                            className="lesson-placement-type"
                            options={lessonPlacementTypesTopPolish}
                            renderInput={(params) => (
                                <TextField {...params} label="Wybór położenia lekcji"/>
                            )}
                            value={values.lessonPlacementType}
                            onChange={(e, newValue) => {
                                setFieldValue(
                                    "lessonPlacementType",
                                    newValue || ""
                                )
                            }}
                            disabled={!values.subject}
                        />

                        <Box className="action-button">
                            <Button variant="contained" type="submit">
                                Zapisz
                            </Button>
                            <Button
                                variant="outlined"
                                onClick={() => resetForm()}
                                sx={{ml: 2}}
                            >
                                Resetuj
                            </Button>
                        </Box>
                    </Form>
                )
            }}
        </Formik>
    )
}

export default GroupSubjectForm
