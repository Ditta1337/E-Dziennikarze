import {useParams} from "react-router";
import {Box} from "@mui/material";
import { get } from "../../../api"
import {useEffect, useState} from "react";
import GeneratedPlansDataGrid from "../../../components/calendar-generated-list-data-grid/GeneratedPlansDataGrid";
import "./GeneratedPlansList.scss"

const fetchPlanCalculationSummary = async (id) => {
    return get(`plan/calculation/summary/${id}`)
}

const GeneratedPlansList = () => {
    const {planId} = useParams()
    const [generatedPlansSummary, setGeneratedPlansSummary] = useState([])

    const updateGeneratedPlansSummary = async () => {
        try{
            const result = await fetchPlanCalculationSummary(planId)
            setGeneratedPlansSummary(result.data)
        } catch(e) {
            console.log(e)
        }
    }

    useEffect(() => {
        updateGeneratedPlansSummary()
    }, [planId]);

    return <Box className="generated-plans-list">
        <GeneratedPlansDataGrid generatedPlansSummary={generatedPlansSummary}/>
    </Box>
}

export default GeneratedPlansList