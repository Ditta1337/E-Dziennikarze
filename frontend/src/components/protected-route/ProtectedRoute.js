import React from "react";
import {Navigate, Outlet} from "react-router";
import {useStore} from "../../store"

const ProtectedRoute = ({ allowedRoles }) => {
    const { user } = useStore();

    if (!user) {
        return <Navigate to="/" replace />;
    }

    if (allowedRoles && !allowedRoles.includes(user.role)) {

        return <Navigate to="/profile" replace />;
    }

    return <Outlet />;
};

export default ProtectedRoute;