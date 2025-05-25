import UsersDataGrid from "../../../components/users-data-grid/UsersDataGrid";
import React, {useEffect, useState} from "react";
import {Typography} from "@mui/material";
import './ListUsers.scss'

function ListUsers() {

    const [users, setUsers] = useState();

    useEffect( () => { // TODO connect to backend
        setUsers( [
            {
                id: 'f3b9e32e-d9c1-4ef3-a82e-0135c2e7b582',
                name: 'Maciej',
                surname: 'Malinowski',
                created_at: '2025-05-25T14:30:00',
                address: 'Ulica 1',
                email: 'ab3@silndvsa.csa',
                contact: 'numer telefonu :P',
                image_base64: 'https://i.pravatar.cc/150?img=1',
                // TODO when conecting to backend add 'data:image/png;base64,' to the beginning of the base 64 code if it's not there
                role: 'ADMIN',
                is_active: true,
            },
            {
                id: 'd2a8e8ec-4b8f-4631-b889-9de7b6d88a42',
                name: 'Anna',
                surname: 'Kowalska',
                created_at: '2025-04-12T09:15:00',
                address: 'Ulica 2',
                email: 'anna.kowalska@example.com',
                contact: '123-456-789',
                image_base64: 'https://i.pravatar.cc/150?img=5',
                role: 'STUDENT',
                is_active: false,
            },
            {
                id: 'a7b3fa6b-21e0-4237-9f1e-1de9a1dfecbd',
                name: 'Tomasz',
                surname: 'Nowak',
                created_at: '2025-03-20T17:45:30',
                address: 'Ulica 3',
                email: 'tomasz.nowak@example.com',
                contact: '+48 501 234 567',
                image_base64: 'https://i.pravatar.cc/150?img=8',
                role: 'GUARDIAN',
                is_active: true,
            },
            {
                id: '9f3d967d-02ab-4b25-bd71-4fa930cb32e0',
                name: 'Zofia',
                surname: 'Wiśniewska',
                created_at: '2025-01-08T11:00:00',
                address: 'Ulica 4',
                email: 'zofia.w@example.com',
                contact: '789-456-123',
                image_base64: 'https://i.pravatar.cc/150?img=11',
                role: 'OFFICE WORKER',
                is_active: true,
            },
        ])
    }, [])

    return <>
        <div className="list-users">
            <Typography className="title">Users List</Typography>
            <UsersDataGrid rows = {users}/>
        </div>
    </>
}

export default ListUsers;