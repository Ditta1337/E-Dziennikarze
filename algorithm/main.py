import fastapi
from fastapi import FastAPI, WebSocket, WebSocketDisconnect
import json
from scheduler import solve_yielding, DataParser, WORKING_DAYS

app = FastAPI()


@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.websocket("/ws")
async def websocket_endpoint(websocket: WebSocket):
    await websocket.accept()

    try:
        input_json = await websocket.receive_text()
        input_data = json.loads(input_json)

        input_groups, input_teachers, input_subjects, input_max_hours_per_day = DataParser.parse_input("input2.json")

        for partial_result in solve_yielding(input_groups, input_teachers, input_subjects,
                                             input_max_hours_per_day, WORKING_DAYS):
            await websocket.send_text(json.dumps(partial_result, ensure_ascii=False, indent=2))

    except WebSocketDisconnect:
        print("Client disconnected")

    except Exception as e:
        await websocket.send_text(json.dumps({"error": str(e)}))
        await websocket.close()