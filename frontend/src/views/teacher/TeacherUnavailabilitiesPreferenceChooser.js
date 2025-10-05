import WeeklyUnavalibleHoursCalendar
    from "../../components/calendar/weekly-unavailable-hours-calendar/WeeklyUnavalibleHoursCalendar";
import {get, post, put, del} from "../../api"
import {useStore} from "../../store";
import UnavailableEventEditor from "../../components/calendar/unavailable-event-editor/UnavailableEventEditor";
import {useState} from "react";
import {AppLocale} from "../../config/localization";

const TeacherUnavailabilitiesPreferenceChooser = () => {

    const teacherId = useStore((state) => state.user.userId)

    const fetchUnavailabilities = async () => {
        return get(`/teacher-unavailability/teacher/${teacherId}`)
    }

    const postUnavailability = async (startTime, endTime, weekDay) => {
        const body = {
            "start_time": startTime,
            "end_time": endTime,
            "week_day": weekDay,
            "teacher_id": teacherId
        }
        return post(`/teacher-unavailability`, body)
    }

    const updateUnavailability = async (id, startTime, endTime, weekDay) => {
        const body = {
            "id": id,
            "start_time": startTime,
            "end_time": endTime,
            "week_day": weekDay,
            "teacher_id": teacherId
        }
        return put(`/teacher-unavailability`, body)
    }

    const deleteUnavailability = async (id) => {
        return del(`/teacher-unavailability/${id}`)
    }

    return <>
        <WeeklyUnavalibleHoursCalendar
            fetchUnavailabilities={fetchUnavailabilities}
            postUnavailability={postUnavailability}
            updateUnavailability={updateUnavailability}
            deleteUnavailability={deleteUnavailability}
        />
    </>
}

export default TeacherUnavailabilitiesPreferenceChooser