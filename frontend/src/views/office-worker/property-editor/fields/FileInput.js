import React from 'react';
import './FileInput.scss';

const FileInput = ({ value, onChange, readOnly }) => {
    return readOnly ? (
        <img 
            src={value} 
            alt="School Logo" 
        />
    ) : (
        <input
            type="file"
            accept="image/*"
            onChange={(e) => {
                const file = e.target.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onloadend = () => onChange(reader.result);
                    reader.readAsDataURL(file);
                }
            }}
        />
    );
};

export default FileInput;
