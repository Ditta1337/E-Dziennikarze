import _ from "lodash";
import { post } from "../../../api";
import {
    StudentRole,
    TeacherRole,
    WorkerRole,
    AdminRole,
    GuardianRole,
} from "../roles";

const baseKeys = [
    "name",
    "surname",
    "email",
    "password",
    "phone",
    "country",
    "address_code",
    "city",
    "address",
    "role",
];

const rolePayloadMap = {
    [StudentRole]: [
        ...baseKeys,
        "guardian_id",
        "can_choose_preferences",
    ],
    [WorkerRole]: [
        ...baseKeys,
        "principal_privileges",
    ],
    [TeacherRole]: [
        ...baseKeys,
        "subjects",
    ],
    [AdminRole]: [
        ...baseKeys,
    ],
    [GuardianRole]: [
        ...baseKeys,
    ],
};

export function submitUser(values) {
    const { role } = values;
    if (!role) {
        throw new Error("Role must be specified before submitting");
    }

    const allowedKeys = rolePayloadMap[role];
    if (!allowedKeys) {
        throw new Error(`Unknown role "${role}"`);
    }

    const payload = _.pick(values, allowedKeys);
    return post(`/${role}`, payload);
}
