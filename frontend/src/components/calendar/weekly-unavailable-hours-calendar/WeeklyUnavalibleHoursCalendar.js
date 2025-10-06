import {Calendar, dateFnsLocalizer, Views} from "react-big-calendar";
import {format, getDay, parse, startOfWeek} from "date-fns";
import {AppLocale} from "../../../config/localization";
import {Alert, Box, Snackbar} from "@mui/material";
import "./WeeklyUnavailableHoursCalendar.scss"
import React, {useEffect, useState} from "react";
import withDragAndDrop from 'react-big-calendar/lib/addons/dragAndDrop'
import 'react-big-calendar/lib/addons/dragAndDrop/styles.css'
import makeDateTimeFromWeekday from "../../../util/calendar/makeDateTimeFromWeekday";
import fetchPropertyByName from "../../../util/property/fetchPropertyByName";
import getDatesWeekday from "../../../util/calendar/getDatesWeekday";
import UnavalibleEvent from "../unavalible-event/UnavalibleEvent";
import UnavailableEventEditor from "../unavailable-event-editor/UnavailableEventEditor";

const DnDCalendar = withDragAndDrop(Calendar)

const defaultMinMaxHours = [new Date(1970, 0, 5, 8, 0), new Date(1970, 0, 5, 15, 0)]

const schoolDayStartTimePropertyName = "schoolDayStartTime"

const schoolDayEndTimePropertyName = "schoolDayEndTime"

const eventsTitle = "Unavalible"

const localizer = dateFnsLocalizer({
    format,
    parse,
    startOfWeek: () => startOfWeek(new Date(), {weekStartsOn: 1}),
    getDay,
    locales: AppLocale.locales,
});

const makeSingularEventFromFetchedResponse = (response) => {
    return {
        id: response.id,
        title: eventsTitle,
        start: makeDateTimeFromWeekday(response.week_day, response.start_time),
        end: makeDateTimeFromWeekday(response.week_day, response.end_time),
        weekDay: response.week_day
    }
}

const makeEventsFromFetchedResponse = (response) => {
    return response.map(event => (makeSingularEventFromFetchedResponse(event)))
}

const WeeklyUnavailableHoursCalendar = ({
                                            fetchUnavailabilities,
                                            postUnavailability,
                                            updateUnavailability,
                                            deleteUnavailability
                                        }) => {
    const [events, setEvents] = useState([])
    const [minMaxHours, setMinMaxHours] = useState(defaultMinMaxHours)
    const [selectedEvent, setSelectedEvent] = useState(null)

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");

    const updateMinMaxHoursByProperty = async () => {
        try {
            const startTime = await fetchPropertyByName(schoolDayStartTimePropertyName)
            const endTime = await fetchPropertyByName(schoolDayEndTimePropertyName)
            const minHours = makeDateTimeFromWeekday("MONDAY", startTime.data.value)
            const maxHours = makeDateTimeFromWeekday("MONDAY", endTime.data.value)
            setMinMaxHours([minHours, maxHours])
        } catch (e) {
            console.error(e)
        }
    }

    const handleSnackbarClose = (event, reason) => {
        if (reason === "clickaway") {
            return;
        }
        setSnackbarOpen(false);
    };

    const createEventOnSlotSelect = async ({start, end}) => {
        try {
            const response = await postUnavailability(format(start, AppLocale.timeFormat), format(end, AppLocale.timeFormat), getDatesWeekday(start))
            const newEvent = makeSingularEventFromFetchedResponse(response.data)
            setEvents([...events, newEvent])
        } catch (error) {
            if (error.response && error.response.status === 409) {
                setSnackbarOpen(true)
                setSnackbarMessage("Konflikt")
            } else {
                setSnackbarOpen(true)
                setSnackbarMessage("Wystąpił nieoczekiwany błąd")
            }
        }
    }

    const updateEvents = async () => {
        try {
            const result = await fetchUnavailabilities()
            setEvents(makeEventsFromFetchedResponse(result.data))
        } catch (e) {
            setSnackbarOpen(true)
            setSnackbarMessage("Wystąpił nieoczekiwany błąd")
        }
    }

    const handleEventEdit = async ({event, start, end}) => {
        try {
            const response = await updateUnavailability(event.id, format(start, AppLocale.timeFormat), format(end, AppLocale.timeFormat), getDatesWeekday(start))
            const eventsWithoutMovedEvent = events.filter((eventToCheck) => eventToCheck.id !== event.id)
            setEvents([...eventsWithoutMovedEvent, makeSingularEventFromFetchedResponse(response.data)])
        } catch (error) {
            if (error.response && error.response.status === 409) {
                setSnackbarOpen(true)
                setSnackbarMessage("Konflikt")
            } else {
                setSnackbarOpen(true)
                setSnackbarMessage("Wystąpił nieoczekiwany błąd")
            }
        }
    }

    const handleEventDeletion = async (event) => {
        try {
            console.log(event)
            await deleteUnavailability(event.id)
            const eventsWithoutDeletedEvent = events.filter((eventToCheck) => eventToCheck.id !== event.id)
            setEvents(eventsWithoutDeletedEvent)
        } catch (e) {
            setSnackbarOpen(true)
            setSnackbarMessage("Wystąpił nieoczekiwany błąd")
        }
    }

    useEffect(() => {
        updateEvents()
        updateMinMaxHoursByProperty()
    }, []);

    return <Box className="calendar">
        <DnDCalendar
            key={`${minMaxHours[0].getTime()}-${minMaxHours[1].getTime()}`}
            localizer={localizer}
            components={{
                event: UnavalibleEvent,
            }}
            events={events}
            onSelectSlot={createEventOnSlotSelect}
            onEventDrop={handleEventEdit}
            onEventResize={handleEventEdit}
            defaultView={Views.WORK_WEEK}
            views={{work_week: true}}
            date={defaultMinMaxHours[0]}
            min={minMaxHours[0]}
            max={minMaxHours[1]}
            selectable
            step={15}
            timeslots={4}
            formats={{
                timeGutterFormat: AppLocale.timeFormat,
                eventTimeRangeFormat: ({start, end}, culture, localizer) =>
                    `${localizer.format(start, AppLocale.timeFormat, culture)} – ${localizer.format(end, AppLocale.timeFormat, culture)}`,
            }}
            onSelectEvent={(event) => {
                setSelectedEvent(event)
            }}
        />

        <UnavailableEventEditor
            event={selectedEvent}
            isOpen={!!selectedEvent}
            onClose={() => setSelectedEvent(null)}
            onEdit={handleEventEdit}
            onDelete={handleEventDeletion}
        />

        <Snackbar
            open={snackbarOpen}
            autoHideDuration={6000}
            onClose={handleSnackbarClose}
            anchorOrigin={{vertical: "bottom", horizontal: "left"}}
        >
            <Alert onClose={handleSnackbarClose} severity="error" sx={{width: '100%'}}>
                {snackbarMessage}
            </Alert>
        </Snackbar>
    </Box>
}

export default WeeklyUnavailableHoursCalendar