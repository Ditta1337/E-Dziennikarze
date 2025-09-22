import React from 'react'
import './NotFound.scss'
import Breadcrumb from "../../components/breadcrumb/Breadcrumb";
import {Typography} from "@mui/material";

function NotFound() {
    return (
        <div className="not-found">
            <Typography className="communicat">Nie znaleziono strony.</Typography>
        </div>
    )
}

export default NotFound;