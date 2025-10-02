import _ from "lodash";
import { post } from "../../../api";
import { StudentRole, TeacherRole } from "../roles";

function preparePayloadByRole(role, values) {
    const fullAddress = _.join(_.compact([
        values.address,
        `${values.address_code} ${values.city}`,
        values.country
    ]), ', ');
    return {
        "name": values.name,
        "surname": values.surname,
        "email": values.email,
        "password": values.password,
        "role": values.role,
        "contact": values.phone,
        "address": fullAddress,
        "active": true,
        "choosing_preferences": role === TeacherRole
    };
}

async function assignGuardian(guardianId, studentId) {
    console.log("assignGuardian");
    const payload = {
        "student_id": studentId,
        "guardian_id": guardianId
    };
    console.log("sg payload", payload);
    try {
        await post(`/student-guardian`, payload);
    } catch (error) {
        throw new Error("Wystąpił błąd podczas przypisywania opiekuna");
    }
}

async function assignSubjects(values, teacherId) {
    console.log("assignSubjects");
    const payload = values.map(subjectId => ({
        "teacher_id": teacherId,
        "subject_id": subjectId
    }));
    console.log("as payload", payload);
    const promises = payload.map(entry => post(`/subject-taught`, entry));
    try {
        await Promise.all(promises);
    } catch (error) {
        throw new Error("Wystąpił błąd podczas przypisywania przedmiotu");
    }
}

export async function submitUser(values) {
    const role = values.role;
    const payload = preparePayloadByRole(role, values);

    try {
        console.log("Submitting user with payload:", payload);
        const response = await post(`/user`, payload);
        const newUserId = response.data.id;

        if (role === StudentRole && values.guardian_id) {
            await assignGuardian(values.guardian_id, newUserId);
        }

        if (role === TeacherRole && values.subjects && values.subjects.length > 0) {
            await assignSubjects(values.subjects, newUserId);
        }
    } catch (error) {
        throw error;
    }
}