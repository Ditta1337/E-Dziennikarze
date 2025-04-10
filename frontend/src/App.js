import {BrowserRouter, Routes, Route} from 'react-router';
import Home from './views/home/Home';
import Gradebook from "./views/gradebook/Gradebook";
import Calendar from "./views/calendar/Calendar";
import NotFound from "./views/not-found/NotFound";


function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route index element={<Home/>}/>
                <Route path="/gradebook" element={<Gradebook/>}/>
                <Route path="/calendar" element={<Calendar/>}/>

                <Route path="*" element={<NotFound/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
