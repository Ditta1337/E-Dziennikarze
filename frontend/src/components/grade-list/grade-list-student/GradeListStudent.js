import React from 'react';
import {useStore} from "../../../store";
import GradeListReadonly from "../grade-list-readonly/GradeListReadonly";

function GradeListStudent() {
    const userId = useStore((state) => state.user.userId)

    return (
        <GradeListReadonly studentId={userId}/>
    )
}

export default GradeListStudent