import withDragAndDrop from "react-big-calendar/lib/addons/dragAndDrop";
import {Calendar, dateFnsLocalizer, Views} from "react-big-calendar";
import {format, getDay, parse, startOfWeek} from "date-fns";
import {AppLocale} from "../../../config/localization";
import {Alert, Box, CircularProgress, Snackbar} from "@mui/material";
import LessonEvent from "../lesson-event/LessonEvent";
import React, {useEffect, useState} from "react";
import fetchPropertyByName from "../../../util/property/fetchPropertyByName";
import makeDateTimeFromWeekday from "../../../util/calendar/makeDateTimeFromWeekday";

const DnDCalendar = withDragAndDrop(Calendar)

const schoolDayStartTimePropertyName = "schoolDayStartTime"

const schoolDayEndTimePropertyName = "schoolDayEndTime"

const localizer = dateFnsLocalizer({
    format,
    parse,
    startOfWeek: () => startOfWeek(new Date(), {weekStartsOn: 1}),
    getDay,
    locales: AppLocale.locales,
});

const defaultMinMaxHours = [new Date(1970, 0, 1, 8, 0), new Date(1970, 0, 1, 15, 0)]

const errorSnackBarMessage = "Wystąpił błąd podczas ładowania planu lekcji"

const findMondayToFriday = (date) => {
    const monday = startOfWeek(date, {weekStartsOn: 1});
    const friday = new Date(monday);
    friday.setDate(monday.getDate() + 4);
    return [monday, friday];
};

const WeeklyModifiableCalendar = ({fetchingId, events, updateEvents, onSelectEvent, onSelectSlot, editMode}) => {
    const [currentWeekStart, setCurrentWeekStart] = useState(startOfWeek(new Date(), {weekStartsOn: 1}))
    const [minMaxHours, setMinMaxHours] = useState(defaultMinMaxHours)

    const [snackbarOpen, setSnackbarOpen] = useState(false)

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

    const handleNavigationAction = async (action, newDate) => {
        const [monday, friday] = findMondayToFriday(newDate)
        setCurrentWeekStart(monday)
        await updateEvents(format(monday, AppLocale.dateFormat), format(friday, AppLocale.dateFormat))
    }

    const handleSnackbarClose = (event, reason) => {
        if (reason === "clickaway") {
            return
        }
        setSnackbarOpen(false)
    }

    useEffect(() => {
        const [monday, friday] = findMondayToFriday(currentWeekStart)
        updateEvents(format(monday, AppLocale.dateFormat), format(friday, AppLocale.dateFormat))
    }, [fetchingId]);

    useEffect(() => {
        updateMinMaxHoursByProperty()
    }, []);


    return (
        <Box className="calendar-container">
            {events === null && currentWeekStart ? (
                <CircularProgress/>
            ) : (
                <Box className="calendar">
                    <Calendar
                        key={`${minMaxHours[0].getTime()}-${minMaxHours[1].getTime()}`}
                        localizer={localizer}
                        events={events || []}
                        components={{
                            event: LessonEvent,
                        }}
                        startAccessor="start"
                        endAccessor="end"
                        defaultView={Views.WORK_WEEK}
                        views={{work_week: true}}
                        step={60}
                        timeslots={1}
                        date={currentWeekStart}
                        style={{height: "100%", width: "100%"}}
                        min={minMaxHours[0]}
                        max={minMaxHours[1]}
                        step={5}
                        timeslots={12}
                        selectable={editMode && !!events && events.length > 0}
                        onSelectSlot={onSelectSlot}
                        toolbar={!editMode}
                        onNavigate={(newDate, view, action) => handleNavigationAction(action, newDate)}
                        onSelectEvent={(event) => {onSelectEvent(event)}}
                        formats={{
                            timeGutterFormat: AppLocale.timeFormat,
                            eventTimeRangeFormat: ({ start, end }, culture, localizer) =>
                                `${localizer.format(start, AppLocale.timeFormat, culture)} – ${localizer.format(end, AppLocale.timeFormat, culture)}`,
                        }}
                    />
                </Box>
            )}

            <Snackbar
                open={snackbarOpen}
                autoHideDuration={6000}
                onClose={handleSnackbarClose}
                anchorOrigin={{vertical: "bottom", horizontal: "left"}}
            >
                <Alert onClose={handleSnackbarClose} severity="error" sx={{width: '100%'}}>
                    {errorSnackBarMessage}
                </Alert>
            </Snackbar>
        </Box>
    );
}

export default WeeklyModifiableCalendar