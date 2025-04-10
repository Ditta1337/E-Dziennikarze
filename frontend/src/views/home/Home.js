import React from 'react';
import './Home.scss';
import {useStore} from "../../store";
import {Button, Chip} from "@mui/material";
import Sidebar from "../../components/sidebar/Sidebar";

function Home() {
    const test = useStore((state) => state.test);
    const increaseTest = useStore((state) => state.increaseTest);
    const decreaseTest = useStore((state) => state.decreaseTest);

    return (
        <div className="home">
            <Sidebar/>
            {/*<Chip label={"Current test: " + test}/>*/}
            {/*<Button onClick={increaseTest} variant="contained">Add to test</Button>*/}
            {/*<Button onClick={decreaseTest} variant="contained">Subtract from test</Button>*/}
        </div>
    );
}

export default Home;