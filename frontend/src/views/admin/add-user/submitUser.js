import _ from "lodash";
import { post } from "../../../api";
import {
    StudentRole,
    TeacherRole
} from "../roles";


function preparePayloadByRole(role, values) {
    const fullAddress = _.join(_.compact([
        values.address,
        `${values.address_code} ${values.city}`,
        values.country
    ]), ', ');
    let payload = {
        "name": values.name,
        "surname": values.surname,
        "email": values.email,
        "password": values.password,
        "role": values.role,
        "contact": values.phone,
        "address": fullAddress,
        "active": true,

    }
    switch (role) {
        case StudentRole: {
            assignGuardian(values)
            break
        }
        case TeacherRole: {
            assignSubjects(values)
            payload = {
                ...payload,
                "choosing_preferences": true
            }
            break
        }
    }
    return payload;
}

function assignGuardian(values) {
    // TODO
    console.log("assignGuardian");
}

function assignSubjects(values) {
    // TODO
    console.log("assignSubjects");
}

export function submitUser(values) {
    const { role } = values;
    if (!role) {
        throw new Error("Role must be specified before submitting");
    }

    const payload = preparePayloadByRole(role, values);
    console.log("Submitting user with payload:", payload);
    return post(`/user`, payload);
}
