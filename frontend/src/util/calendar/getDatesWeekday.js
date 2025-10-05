import {weekdays} from "./weekdays";

const getDatesWeekday = (date) => {
    const dayIndex = new Date(date).getDay()
    const adjustedIndex = (dayIndex + 6) % 7
    return weekdays[adjustedIndex]
}

export default getDatesWeekday