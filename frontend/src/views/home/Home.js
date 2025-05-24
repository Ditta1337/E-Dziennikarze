import React from 'react';
import './Home.scss';
import {useStore} from "../../store";
import {Box, Button, Chip} from "@mui/material";
import ImageAdder from "../../components/image-adder/ImageAdder";

function Home() {
    const test = useStore((state) => state.test);
    const increaseTest = useStore((state) => state.increaseTest);
    const decreaseTest = useStore((state) => state.decreaseTest);

    return (
        <Box className="home">
            <Chip className="text-value" label={`Test Value: ${test}`}/>
            <Box className="button-container">
                <Button className="test-button success" onClick={increaseTest}>
                    Increase
                </Button>
                <Button className="test-button error" onClick={decreaseTest}>
                    Decrease
                </Button>
            </Box>

            <ImageAdder/>
        </Box>
    );
}

export default Home;