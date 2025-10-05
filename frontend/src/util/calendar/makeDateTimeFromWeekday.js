import { weekdays } from "./weekdays"

const baseDate = new Date(1970, 0, 5);

const makeDateTimeFromWeekday = (weekDay, time, date = baseDate) => {
    const dayIndex = weekdays.indexOf(weekDay)
    if (dayIndex === -1) {
        throw new Error(`Invalid day: ${weekDay}`);
    }
    const result = new Date(date)
    result.setDate(baseDate.getDate() + dayIndex)
    const [hours, minutes, seconds] = time.split(":").map(Number)
    result.setHours(hours)
    result.setMinutes(minutes)
    result.setSeconds(seconds)
    return result
}

export default makeDateTimeFromWeekday