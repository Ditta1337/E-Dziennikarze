import {useCallback, useEffect, useMemo, useState} from "react"
import {Autocomplete, Box, Button, Chip, TextField} from "@mui/material"
import {Formik, Form} from "formik"
import * as Yup from "yup"
import "./GroupSubjectForm.scss"
import {lessonPlacementTypesTopPolish} from "../lesson-placement-type/LessonPlacementType"

const GroupSubjectForm = ({
                              configurationData,
                              setConfigurationData,
                              fetchGroupSubjectData,
                              fetchRooms,
                              displaySnackBarMessage
                          }) => {
    const [groupSubjectData, setGroupSubjectData] = useState([])
    const [rooms, setRooms] = useState([])

    const updateGroupSubjectData = useCallback(async () => {
        try {
            const response = await fetchGroupSubjectData()
            setGroupSubjectData(response?.data || [])
        } catch (e) {
            console.error(e)
            displaySnackBarMessage("Wystąpił błąd podczas pobierania danych o grupach")
        }
    }, [fetchGroupSubjectData])

    const updateRooms = useCallback(async () => {
        try {
            const response = await fetchRooms()
            setRooms(response?.data || [])
        } catch (e) {
            displaySnackBarMessage("Wystąpił błąd podczas pobierania danych o pokojach.")
            console.error(e)
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
        return Array.from(map.values()).sort((a, b) =>
            a.group_code.localeCompare(b.group_code)
        )
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
            return Array.from(map.values()).sort((a, b) =>
                a.name.localeCompare(b.name)
            )
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

    useEffect(() => {
    }, [configurationData])

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
            validateOnChange={false}
            validateOnBlur={false}
            onSubmit={(values, {setTouched, validateForm, resetForm}) => {
                validateForm(values).then((errors) => {
                    if (Object.keys(errors).length) {
                        setTouched({rooms: true})
                    }
                })
                const payload = {
                    subject_id: values.subject?.id || null,
                    lessons_per_week:
                        values.lessonsPerWeek === 0 ? 0 : Number(values.lessonsPerWeek),
                    max_lessons_per_day:
                        values.maxLessonsPerDay === 0 ? 0 : Number(values.maxLessonsPerDay),
                    type: values.lessonPlacementType?.value || "ANY",
                    room: {
                        allowed: values.rooms.map((r) => r.id),
                        preferred: values.preferredRooms.map((r) => r.id),
                        dispreferred: values.unpreferredRooms.map((r) => r.id),
                    },
                }
                setConfigurationData((prev) => {
                    const updated = structuredClone(prev)
                    const groupToUpdate = updated.configuration.groups.find(
                        (g) => g.group_id === values.group.group_id
                    )
                    if (!groupToUpdate) return prev
                    const subjectToUpdate = groupToUpdate.subjects.find(
                        (s) => s.subject_id === values.subject.id
                    )
                    if (subjectToUpdate) {
                        subjectToUpdate.lessons_per_week =
                            payload.lessons_per_week === 0 ? null : payload.lessons_per_week
                        subjectToUpdate.max_lessons_per_day =
                            payload.max_lessons_per_day === 0 ? null : payload.max_lessons_per_day
                        subjectToUpdate.type = payload.type
                        subjectToUpdate.room.allowed = payload.room.allowed
                        subjectToUpdate.room.preferred = payload.room.preferred
                        subjectToUpdate.room.dispreferred = payload.room.dispreferred
                    }
                    return updated
                })
                resetForm({
                    values: {
                        group: values.group,
                        subject: "",
                        rooms: [],
                        preferredRooms: [],
                        unpreferredRooms: [],
                        lessonsPerWeek: "",
                        maxLessonsPerDay: "",
                        lessonPlacementType: "",
                    },
                })
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

                const fillGroupSubjectConfiguration = (newSubjectValue) => {
                    const existingConfiguration = findGroupSubjectFilledConfiguration(newSubjectValue)
                    if (existingConfiguration) {
                        setFieldValue(
                            "lessonsPerWeek",
                            existingConfiguration.lessons_per_week > 0 ? existingConfiguration.lessons_per_week : ""
                        )
                        setFieldValue(
                            "maxLessonsPerDay",
                            existingConfiguration.max_lessons_per_day > 0 ? existingConfiguration.max_lessons_per_day : ""
                        )
                        setFieldValue(
                            "lessonPlacementType",
                            lessonPlacementTypesTopPolish.find(
                                (t) => t.value === existingConfiguration.type) || ""
                        )
                        const allowedRoomIds = existingConfiguration.room.allowed || []
                        const preferredRoomIds = existingConfiguration.room.preferred || []
                        const dispreferredRoomIds = existingConfiguration.room.dispreferred || []

                        const allowedRooms = rooms.filter((r) =>
                            allowedRoomIds.includes(r.id)
                        )
                        const preferredRooms = rooms.filter((r) =>
                            preferredRoomIds.includes(r.id)
                        )
                        const unpreferredRooms = rooms.filter((r) =>
                            dispreferredRoomIds.includes(r.id)
                        )

                        setFieldValue("rooms", allowedRooms)
                        setFieldValue("preferredRooms", preferredRooms)
                        setFieldValue("unpreferredRooms", unpreferredRooms)
                    } else {
                        setFieldValue("rooms", [])
                        setFieldValue("preferredRooms", [])
                        setFieldValue("unpreferredRooms", [])
                        setFieldValue("lessonPlacementType", "")
                    }

                }

                const findGroupSubjectFilledConfiguration = (newSubjectValue) => {
                    if (newSubjectValue === null) return
                    const groupSubjectData = configurationData.configuration.groups.find((groupSubjectPair) => groupSubjectPair.group_id === values.group.group_id)
                    return groupSubjectData.subjects.find((subject) => subject.subject_id === newSubjectValue.id)
                }

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
                                    setFieldValue("maxLessonsPerDay", "")
                                    setFieldValue("lessonsPerWeek", "")
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
                                    fillGroupSubjectConfiguration(newValue)
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
                            <Button
                                variant="contained"
                                type="submit"
                                disabled={configurationData?.calculated || false}
                            >
                                Zatwierdź
                            </Button>
                            <Button
                                variant="outlined"
                                onClick={() => resetForm()}
                                sx={{ml: 2}}
                                disabled={configurationData?.calculated || false}
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
