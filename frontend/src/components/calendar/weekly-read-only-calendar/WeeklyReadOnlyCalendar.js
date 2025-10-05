import React, {useEffect, useState} from "react";
import {Calendar, Views, dateFnsLocalizer} from "react-big-calendar";
import {format, parse, startOfWeek, getDay} from "date-fns";
import "react-big-calendar/lib/css/react-big-calendar.css";
import {AppLocale} from "../../../config/localization";
import {Alert, Box, CircularProgress, Snackbar} from "@mui/material";
import LessonEvent from "../lesson-event/LessonEvent";
import "./WeeklyReadOnlyCalendar.scss"

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

const makeDateFromDateAndTimeString = (date, time) => {
    return new Date(`${date}T${time}`);
}

const roundTimeUpByMinutes = (date) => {
    if ( date.getMinutes() > 0) {
        return new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours() + 1, 0, 0)
    }
    return date
}

const roundTimeDownByMinutes = (date) => {
    if( date.getMinutes() > 0) {
        return new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), 0, 0)
    }
    return date
}

const makeCalendarEventsFromFetchedResponse = (response) => {
    let id = 0;
    return response.map(lessonData => ({
        id: id++,
        title: lessonData.subject,
        start: makeDateFromDateAndTimeString(lessonData.date, lessonData.start_time),
        end: makeDateFromDateAndTimeString(lessonData.date, lessonData.end_time),
        teacherId: lessonData.teacher_id,
        room: lessonData.room
    }))
}

const WeeklyReadOnlyCalendar = ({ fetchLessons, onSelectEvent }) => {
    const [calendarEvents, setCalendarEvents] = useState(null);
    const [currentWeekStart, setCurrentWeekStart] = useState(startOfWeek(new Date(), {weekStartsOn: 1}))
    const [minMaxHours, setMinMaxHours] = useState(defaultMinMaxHours)

    const [snackbarOpen, setSnackbarOpen] = useState(false);

    const updateLessons = async (monday, friday) => {
        try {
            const response = await fetchLessons(format(monday, AppLocale.dateFormat), format(friday, AppLocale.dateFormat))
            const calendarEvents = makeCalendarEventsFromFetchedResponse(response.data)
            setCalendarEvents(calendarEvents)
        } catch (e) {
            setCalendarEvents([])
            setSnackbarOpen(true)
        }
    };

    const computeMinMaxFromEvents = () => {
        let [minDefault, maxDefault] = defaultMinMaxHours;
        let minTime = minDefault;
        let maxTime = maxDefault;
        calendarEvents.forEach((evt) => {
            const s = evt.start;
            const e = evt.end;
            const sTime = new Date(1970, 0, 1, s.getHours(), s.getMinutes(), s.getSeconds());
            const eTime = new Date(1970, 0, 1, e.getHours(), e.getMinutes(), e.getSeconds());
            if (sTime < minTime) {
                minTime = sTime;
            }
            if (eTime > maxTime) {
                maxTime = eTime;
            }
        });
        setMinMaxHours([roundTimeDownByMinutes(minTime), roundTimeUpByMinutes(maxTime)])
    };

    const handleNavigationAction = async (action, newDate) => {
        const [monday, friday] = findMondayToFriday(newDate);
        setCurrentWeekStart(monday);
        await updateLessons(monday, friday);
        computeMinMaxFromEvents()
    };

    const handleSnackbarClose = (event, reason) => {
        if (reason === "clickaway") {
            return;
        }
        setSnackbarOpen(false);
    };

    useEffect(() => {
        const [monday, friday] = findMondayToFriday(new Date())
        updateLessons(monday, friday)
    }, []);

    return (
        <Box className="calendar-container">
            {calendarEvents === null && currentWeekStart ? (
                <CircularProgress/>
            ) : (
                <Box className="calendar">
                    <Calendar
                        key={`${minMaxHours[0].getTime()}-${minMaxHours[1].getTime()}`}
                        localizer={localizer}
                        events={calendarEvents || []}
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
                anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
            >
                <Alert onClose={handleSnackbarClose} severity="error" sx={{ width: '100%' }}>
                    {errorSnackBarMessage}
                </Alert>
            </Snackbar>
        </Box>
    );
};


export default WeeklyReadOnlyCalendar;
