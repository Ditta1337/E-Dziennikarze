import React from "react";
import "./Gradebook.scss";
import { useStore } from "../../store";
import {Box} from "@mui/material";

function Gradebook() {
    const { user, token, refresh, logout } = useStore();

    return (
        <Box className="gradebook">
            <pre>{JSON.stringify(user, null, 2)}</pre>
            <hr/>
            <div>Token: {token}</div>
            <div>Refresh: {refresh}</div>

            <button onClick={logout}>Logout</button>

        </Box>
    )
}

export default Gradebook;