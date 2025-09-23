from fastapi import FastAPI, Request
import uvicorn
from schemas import ScheduleConfig
from scheduler import solve
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
    solve(schedule_config)

@app.post("/echo")
async def echo(request: Request):
    body = await request.body()
    return {"echo": body.decode("utf-8")}



if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
# @app.post("/solve")
# async def solve(data,endpont):
#     solve(data,endpoint)
#     return {"message":"started"}


# @app.post("/status")
# async def solve(data,endpont):
#     status="solving"
#     return {"status":status}
