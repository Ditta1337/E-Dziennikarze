import _ from "lodash";
import {patch} from "../../api";

export function submitPhoto(photo, id) {
    const payload = _.pick({image_base64: photo});
    return patch(`/user/photo/${id}`, payload); //TODO add endpoint for patching photo
}

export function submitPassword(password, id) {
    const payload = _.pick({password: password})
    return patch(`/user/password/${id}`, payload); //TODO same as before
}
