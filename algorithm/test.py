from fastapi.testclient import TestClient

from .main import app, solver_status

client=TestClient(app)

def setup():
    pass

def test_solve():
    pass
