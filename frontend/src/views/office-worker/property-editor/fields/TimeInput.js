import React from 'react';
import { Field } from 'formik';
import { TimePicker } from '@mui/x-date-pickers/TimePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';
import './TimeInput.scss';

const TimeInput = ({ name, isEditing }) => {
    return (
        <Field name={name}>
            {({ field, form }) => (
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <TimePicker
                        value={field.value ? dayjs(field.value, 'HH:mm:ss') : null}
                        onChange={(newValue) => form.setFieldValue(name, newValue ? newValue.format('HH:mm:ss') : '')}
                        disabled={!isEditing}
                        renderInput={(params) => <input {...params.inputProps} />}
                    />
                </LocalizationProvider>
            )}
        </Field>
    );
};

export default TimeInput;
