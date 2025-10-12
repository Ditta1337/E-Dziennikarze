import React from 'react';
import { Typography } from '@mui/material';
import GradeListTeacher from '../grade-list-teacher/GradeListTeacher';
import './GradeListModal.scss';
import Modal from "../../modal/Modal";

const GradeListModal = ({ isOpen, onClose, groupName, subjectName, groupId, subjectId }) => {
    return (
        <Modal
            className="grade-modal"
            isOpen={isOpen}
            onClose={onClose}
        >
            {isOpen && groupId && subjectId ? (
                <>
                    <Typography className="title">
                        {`Oceny: ${subjectName} | Grupa: ${groupName}`}
                    </Typography>
                    <GradeListTeacher groupId={groupId} subjectId={subjectId} />
                </>
            ) : null}
        </Modal>
    );
};

export default GradeListModal;