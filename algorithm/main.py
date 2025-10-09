from fastapi import FastAPI
import json
import uvicorn
from schemas import ScheduleConfig
from scheduler import Scheduler
app = FastAPI()


# app.add_middleware(
#     CORSMiddleware,
#     allow_origins=["*"],
#     allow_credentials=True,
#     allow_methods=["*"],
#     allow_headers=["*"],
# )



@app.post("/solve")
async def solve_endpoint(schedule_config: ScheduleConfig):
    scheduler = Scheduler(schedule_config)
    scheduler.build()
    scheduler.solve()

@app.get("/goals")
async def echo():
    with open("algorithm/data/goals.json") as f:
        goals=json.load(f)
        return {"goals": goals}



if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
