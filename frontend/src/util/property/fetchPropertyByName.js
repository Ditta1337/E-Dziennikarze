import { get } from "../../api"

const fetchPropertyByName = (name) => {
    return get(`/property/name/${name}`)
}

export default fetchPropertyByName