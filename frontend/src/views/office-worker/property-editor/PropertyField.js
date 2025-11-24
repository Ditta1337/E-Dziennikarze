import React from 'react';
import { Field } from 'formik';
import PhoneInput from "../../../components/form/fields/phone-input/PhoneInput.js";
import FileInput from './fields//FileInput';
import TimeInput from './fields//TimeInput';

const fieldLabels = {
    schoolPhoneNumber: "Telefon szkoły",
    schoolFullName: "Pełna nazwa szkoły",
    schoolDayStartTime: "Godzina rozpoczęcia dnia",
    schoolDayEndTime: "Godzina zakończenia dnia",
    lessonStartTime: "Godzina rozpoczęcia lekcji",
    lessonEndTime: "Godzina zakończenia lekcji",
    lessonDurationMinutes: "Czas trwania lekcji (min)",
    latestStartingLesson: "Najpóźniejsza lekcja",
    shortBreakDurationMinutes: "Krótka przerwa (min)",
    longBreakDurationMinutes: "Długa przerwa (min)",
    longBreakAfterLessons: "Długa przerwa po lekcjach",
    lessonsPerDay: "Liczba lekcji dziennie",
    maxLessonsPerDay: "Maksymalna liczba lekcji dziennie",
    allowTeacherPickPreferences: "Pozwól nauczycielowi wybierać preferencje",
    schoolLogoBase64: "Logo szkoły"
};

const PropertyField = ({ prop, isEditing, setFieldValue }) => {
    const readOnly = !isEditing;
    const label = fieldLabels[prop.name] || prop.name;

    switch(prop.name) {
        case "schoolPhoneNumber":
            return <PhoneInput name={prop.name} readOnly={readOnly} />;

        case "lessonStartTime":
        case "lessonEndTime":
            return <TimeInput name={prop.name} isEditing={isEditing} />;

        case "lessonDurationMinutes":
        case "latestStartingLesson":
        case "shortBreakDurationMinutes":
        case "longBreakDurationMinutes":
        case "longBreakAfterLessons":
        case "lessonsPerDay":
        case "maxLessonsPerDay":
            return <Field name={prop.name}>
                {({ field }) => <input type="number" {...field} readOnly={readOnly} />}
            </Field>;

        case "allowTeacherPickPreferences":
            return <Field name={prop.name} type="checkbox">
                {({ field }) => <input type="checkbox" {...field} checked={field.value} disabled={readOnly} />}
            </Field>;

        case "schoolLogoBase64":
            return <FileInput value={prop.value} onChange={(val) => setFieldValue(prop.name, val)} readOnly={!isEditing} />;

        default:
            return <Field name={prop.name}>
                {({ field }) => <input type="text" {...field} readOnly={readOnly} />}
            </Field>;
    }
};

export { fieldLabels };
export default PropertyField;
