import React from "react";
import {Navigate, Outlet} from "react-router";
import {useStore} from "../../store"

const ProtectedRoute = () => {
    const {user} = useStore();

    if (!user) {
        return <Navigate to="/" replace/>;
    }

    return <Outlet/>;
};

export default ProtectedRoute;