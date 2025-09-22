import _ from "lodash";
import {post, del, put} from "../../api";
import {StudentRole, TeacherRole} from "../../views/admin/roles";

function preparePayloadByRole(role, values) {
    const fullAddress = _.join(_.compact([
        values.address,
        values.address_code,
        values.city,
        values.country
    ]), ';');
    return {
        "name": values.name,
        "surname": values.surname,
        "email": values.email,
        "password": values.password,
        "role": values.role,
        "contact": values.phone,
        "address": fullAddress,
        "image_base64": values.image_base64,
        "active": true,
        "choosing_preferences": role === TeacherRole
    };
}

async function assignGuardians(guardianIds, studentId) {
    const payload = guardianIds.map(guardianId => ({
        "student_id": studentId,
        "guardian_id": guardianId
    }));
    const promises = payload.map(entry => post(`/student-guardian`, entry));
    try {
        await Promise.all(promises);
    } catch (error) {
        throw new Error("Wystąpił błąd podczas przypisywania opiekuna");
    }
}

export async function deleteGuardians(guardianIds, studentId) {
    const promises = guardianIds.map(guardianId =>
        del(`/student-guardian/guardian/${guardianId}/student/${studentId}`)
    );
    try {
        await Promise.all(promises);
    } catch (error) {
        throw new Error("Wystąpił błąd podczas usuwania opiekuna");
    }
}

async function assignSubjects(values, teacherId) {
    const payload = values.map(subjectId => ({
        "teacher_id": teacherId,
        "subject_id": subjectId
    }));
    const promises = payload.map(entry => post(`/subject-taught`, entry));
    try {
        await Promise.all(promises);
    } catch (error) {
        throw new Error("Wystąpił błąd podczas przypisywania przedmiotu");
    }
}

async function deleteSubjectsTaught(subjectIds, teacherId) {
    const promises = subjectIds.map(subjectId =>
        del(`/subject-taught/teacher/${teacherId}/subject/${subjectId}`)
    );
    try {
        await Promise.all(promises);
    } catch (error) {
        throw new Error("Wystąpił błąd podczas usuwania przedmiotu");
    }
}

export async function submitUser(values) {
    const role = values.role;
    const payload = preparePayloadByRole(role, values);

    try {
        const response = await post(`/user`, payload);
        const newUserId = response.data.id;

        if (role === StudentRole && values.guardian_id) {
            await assignGuardians(values.guardian_id, newUserId);
        }

        if (role === TeacherRole && values.subjects && values.subjects.length > 0) {
            await assignSubjects(values.subjects, newUserId);
        }
    } catch (error) {
        throw error;
    }
}

export async function updateUser(values, userId, currentGuardiansIds, currentSubjectsIds) {
    const role = values.role;
    const payload = preparePayloadByRole(role, values);
    payload.id = userId;

    try {
        await put(`/user`, payload);

        if (role === StudentRole && values.guardian_ids) {
            const guardianIdsToAdd = _.difference(values.guardian_ids, currentGuardiansIds);
            const guardianIdsToRemove = _.difference(currentGuardiansIds, values.guardian_ids);
            await assignGuardians(guardianIdsToAdd, userId);
            await deleteGuardians(guardianIdsToRemove, userId);
        }

        if (role === TeacherRole && values.subjects && values.subjects.length > 0) {
            const subjectIdsToAdd = _.difference(values.subjects, currentSubjectsIds);
            const subjectIdsToRemove = _.difference(currentSubjectsIds, values.subjects);
            await assignSubjects(subjectIdsToAdd, userId);
            await deleteSubjectsTaught(subjectIdsToRemove, userId);
        }
    } catch (error) {
        throw error;
    }
}
