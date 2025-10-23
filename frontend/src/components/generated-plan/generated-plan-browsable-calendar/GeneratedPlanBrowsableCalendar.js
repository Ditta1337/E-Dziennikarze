import {useEffect, useState} from "react";
import {Box} from "@mui/material";
import {Calendar, dateFnsLocalizer, Views} from "react-big-calendar";
import {format, getDay, parse, startOfWeek} from "date-fns";
import {AppLocale} from "../../../config/localization";
import withDragAndDrop from "react-big-calendar/lib/addons/dragAndDrop";
import "./GeneratedPlanBrowsableCalendar.scss"
import makeDateTimeFromWeekday from "../../../util/calendar/makeDateTimeFromWeekday";

const DnDCalendar = withDragAndDrop(Calendar)

const localizer = dateFnsLocalizer({
    format,
    parse,
    startOfWeek: () => startOfWeek(new Date(), {weekStartsOn: 1}),
    getDay,
    locales: AppLocale.locales,
});

const enrichGeneratedPlan = (generatedPlan, groups, teachers, subjects, rooms) => {
    const groupMap = Object.fromEntries(groups.map(g => [g.id, g.group_code]))
    const teacherMap = Object.fromEntries(teachers.map(t => [t.id, `${t.name} ${t.surname}`]))
    const subjectMap = Object.fromEntries(subjects.map(s => [s.id, s.name]))
    const roomMap = Object.fromEntries(rooms.map(r => [r.id, r.room_code]))
    const calculation = generatedPlan.calculation.map(lesson => ({
        active: lesson.active,
        start_time: lesson.start_time,
        end_time: lesson.end_time,
        group_id: lesson.group_id,
        group_name: groupMap[lesson.group_id] || "",
        room_id: lesson.room_id,
        room_code: roomMap[lesson.room_id] || "",
        subject_id: lesson.subject_id,
        subject_name: subjectMap[lesson.subject_id] || "",
        teacher_id: lesson.teacher_id,
        teacher_name: teacherMap[lesson.teacher_id] || "",
        week_day: lesson.week_day
    }))
    return {
        ...generatedPlan,
        calculation
    }
}

const defaultMinMaxHours = [new Date(1970, 0, 5, 6, 0), new Date(1970, 0, 5, 15, 0)]

const GeneratedPlanBrowsableCalendar = ({id, fetchGeneratedPlan, groupsToDisplay, teacherToDisplay, fetchGroups, fetchTeachers, fetchSubjets, fetchRooms}) => {
    const [generatedPlanData, setGeneratedPlanData] = useState()
    const [events, setEvents] = useState([])

    const updateGeneratedPlan = async () => {
        try {
            const generatedPlanResult = await fetchGeneratedPlan(id)
            const groupsResult = await fetchGroups()
            const teachersResult = await fetchTeachers()
            const subjectsResult = await fetchSubjets()
            const roomsResult = await fetchRooms()

            const enrichedPlan = enrichGeneratedPlan(
                generatedPlanResult.data,
                groupsResult.data,
                teachersResult.data,
                subjectsResult.data,
                roomsResult.data
            )

            setGeneratedPlanData(enrichedPlan)
        } catch (e) {
            console.log(e)
        }
    }


    const createEventsForGroups = () => {
        if (!generatedPlanData?.calculation || groupsToDisplay.size === 0) return []

        const filteredLessons = generatedPlanData.calculation.filter(lesson =>
            groupsToDisplay.includes(lesson.group_id)
        )

        const events = filteredLessons.map(lesson => ({
            id: lesson.id,
            title: `${lesson.subject_name} (${lesson.teacher_name})`,
            start: makeDateTimeFromWeekday(lesson.week_day, lesson.start_time),
            end: makeDateTimeFromWeekday(lesson.week_day, lesson.end_time),
            resource: lesson,
        }))

        setEvents(events)
    }

    const createEventsForTeacher = () => {
        if (!generatedPlanData?.calculation || teacherToDisplay === null) return []

        const filteredLessons = generatedPlanData.calculation.filter(lesson =>
            lesson.teacher_id === teacherToDisplay
        )

        const events = filteredLessons.map(lesson => ({
            id: lesson.id,
            title: `${lesson.subject_name} (${lesson.group_name})`,
            start: makeDateTimeFromWeekday(lesson.week_day, lesson.start_time),
            end: makeDateTimeFromWeekday(lesson.week_day, lesson.end_time),
            resource: lesson,
        }))

        setEvents(events)
    }

    useEffect(() => {
        updateGeneratedPlan()
    }, []);

    useEffect(() => {
        createEventsForGroups()
    }, [generatedPlanData, groupsToDisplay]);

    useEffect(() => {
        createEventsForTeacher()
    }, [generatedPlanData, teacherToDisplay]);

    useEffect(() => {
        console.log(events)
        console.log(teacherToDisplay)
        console.log(groupsToDisplay)
    }, [events]);

    return <Box className="generated-plan-browsable-calendar">
        <DnDCalendar
            localizer={localizer}
            defaultView={Views.WORK_WEEK}
            views={{work_week: true}}
            date={defaultMinMaxHours[0]}
            min={defaultMinMaxHours[0]}
            max={defaultMinMaxHours[1]}
            events={events}
            formats={{
                timeGutterFormat: AppLocale.timeFormat,
                eventTimeRangeFormat: ({start, end}, culture, localizer) =>
                    `${localizer.format(start, AppLocale.timeFormat, culture)} – ${localizer.format(end, AppLocale.timeFormat, culture)}`,
            }}
            toolbar={false}
        />
    </Box>
}

export default GeneratedPlanBrowsableCalendar