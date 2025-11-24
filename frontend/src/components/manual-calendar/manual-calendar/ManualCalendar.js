import withDragAndDrop from "react-big-calendar/lib/addons/dragAndDrop";
import {Calendar, dateFnsLocalizer, Views} from "react-big-calendar";
import {format, getDay, parse, startOfWeek} from "date-fns";
import {AppLocale} from "../../../config/localization";
import {useEffect, useState} from "react";
import makeDateTimeFromWeekday from "../../../util/calendar/makeDateTimeFromWeekday";
import options from "../group-teacher-chooser/GroupTeacherOptions"
import fetchPropertyByName from "../../../util/property/fetchPropertyByName";

const DnDCalendar = withDragAndDrop(Calendar)

const defaultMinMaxHours = [new Date(1970, 0, 5, 8, 0), new Date(1970, 0, 5, 15, 0)]

const localizer = dateFnsLocalizer({
    format,
    parse,
    startOfWeek: () => startOfWeek(new Date(), {weekStartsOn: 1}),
    getDay,
    locales: AppLocale.locales,
});

const makeEventsFromLessonData = (lessons) => {
    return lessons.map(lessonData => ({
        id: lessonData.id,
        title: lessonData.subject,
        start: makeDateTimeFromWeekday(lessonData.week_day, lessonData.start_time),
        end: makeDateTimeFromWeekday(lessonData.week_day, lessonData.end_time),
        teacher: lessonData.teacher,
        teacherId: lessonData.teacher_id,
        roomId: lessonData.room_id,
        room: lessonData.room,
        subjectId: lessonData.subject_id,
        subject: lessonData.subject,
        groupId: lessonData.group_id,
        groupCode: lessonData.group
    }))
}

const ManualCalendar = ({lessonData, editLesson, perspective, handleEventSelect, handleEventCreation, displaySnackbarMessage}) => {
    const [events, setEvents] = useState([]);
    const [minMaxHours, setMinMaxHours] = useState(defaultMinMaxHours)

    const updateMinMaxHours = async () => {
        try{
            const startHoursResult = await fetchPropertyByName("schoolDayStartTime")
            const endHoursResult = await fetchPropertyByName("schoolDayEndTime")
            const startHours = makeDateTimeFromWeekday("MONDAY", startHoursResult.data.value)
            const endHours = makeDateTimeFromWeekday("MONDAY", endHoursResult.data.value)
            setMinMaxHours([startHours, endHours])
        } catch(e) {
            displaySnackbarMessage("Wystąpił błąd podczas pobierania danych o rozpoczęciu i zakończeniu dnia.")
        }
    }

    const setCalendarEventsFittingPerspective = () => {
        if (lessonData == null) return
        if (perspective == null) {
            setEvents([])
        } else if (perspective.chosenPerspective === options.TeacherPerspective) {
            const lessons = lessonData.filter(lesson => lesson.teacher_id === perspective.id)
            setEvents(makeEventsFromLessonData(lessons))
        } else if (perspective.chosenPerspective === options.GroupPerspective) {
            const lessons = lessonData.filter(lesson => lesson.group_id === perspective.id)
            setEvents(makeEventsFromLessonData(lessons))
        }
    }

    const handleEventEdit = ({event, start, end}) => {
        const eventsWithoutMovedEvent = events.filter((eventToCheck) => eventToCheck.id !== event.id)
        const modifiedEvent = {
            ...event,
            start: start,
            end: end
        }
        setEvents([...eventsWithoutMovedEvent, modifiedEvent])
        console.log(modifiedEvent)
        editLesson(modifiedEvent)
    }

    useEffect(() => {
        setCalendarEventsFittingPerspective()
    }, [lessonData, perspective]);

    useEffect(() => {
        updateMinMaxHours()
    }, []);

    return <>
        <DnDCalendar
            localizer={localizer}
            defaultView={Views.WORK_WEEK}
            views={{work_week: true}}
            events={events}
            date={defaultMinMaxHours[0]}
            min={minMaxHours[0]}
            max={minMaxHours[1]}
            step={5}
            timeslots={12}
            selectable={!!perspective & !!lessonData}
            onEventResize={handleEventEdit}
            onEventDrop={handleEventEdit}
            onSelectEvent={handleEventSelect}
            onSelectSlot={handleEventCreation}
            toolbar={false}
            formats={{
                timeGutterFormat: AppLocale.timeFormat,
                eventTimeRangeFormat: ({start, end}, culture, localizer) =>
                    `${localizer.format(start, AppLocale.timeFormat, culture)} – ${localizer.format(end, AppLocale.timeFormat, culture)}`,
            }}
        />
    </>
}

export default ManualCalendar