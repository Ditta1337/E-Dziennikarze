import './App.scss';
import {Button} from "@mui/material";

function App() {
    return (
        <div className="App">
            <Button variant="text" className="button">Text</Button>
            <Button variant="text" disabled={true} className="button">Text</Button>
        </div>
    );
}

export default App;
