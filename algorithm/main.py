from fastapi import FastAPI, Request, Depends, HTTPException,BackgroundTasks 
import json
import uvicorn
from exceptions.infeasable_model_exception import InfeasableModelException
from schemas import ScheduleConfig
from scheduler import Scheduler
from solver_status import SolverStatus

app = FastAPI()
solver_status = SolverStatus.IDLE

# app.add_middleware(
#     CORSMiddleware,
#     allow_origins=["*"],
#     allow_credentials=True,
#     allow_methods=["*"],
#     allow_headers=["*"],
# )

async def get_and_log_schedule_config(request: Request) -> ScheduleConfig:
    body_bytes = await request.body()
    body_str = body_bytes.decode()

    print("--- RAW REQUEST BODY ---")
    print(body_str)
    print("------------------------")
    return ScheduleConfig.model_validate_json(body_str)

@app.post("/solve")
async def solve_endpoint(background_tasks: BackgroundTasks, schedule_config: ScheduleConfig = Depends(get_and_log_schedule_config)):
    global solver_status, solver_error
    solver_status = SolverStatus.CALCULATING
    solver_error = None

    scheduler = Scheduler(schedule_config)

    def run_solver():
        global solver_status, solver_error
        try:
            scheduler.build()
            scheduler.solve()
        except InfeasableModelException as e:
            solver_error = str(e)
        finally:
            solver_status = SolverStatus.IDLE

    background_tasks.add_task(run_solver)
    return {"message": "Solver started in background"}



@app.get("/status")
async def status():
    return {"status": solver_status}

@app.get("/goals")
async def echo():
    with open("data/goals.json") as f:
        goals=json.load(f)
        return goals



if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
