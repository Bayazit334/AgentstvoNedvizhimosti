from fastapi import FastAPI
from sqlalchemy import text

from database import engine
from routers.auth import router as auth_router
from routers.properties import router as properties_router
from routers.deals import router as deals_router
from routers.documents import router as documents_router


app = FastAPI(
    title="Агентство недвижимости",
    description="REST API информационной системы агентства недвижимости",
    version="1.0.0"
)

app.include_router(auth_router)
app.include_router(properties_router)
app.include_router(deals_router)
app.include_router(documents_router)


@app.get("/")
def root():
    return {
        "message": "Сервер агентства недвижимости работает"
    }


@app.get("/db-check")
def check_database():
    with engine.connect() as connection:
        connection.execute(text("SELECT 1"))

    return {
        "message": "Подключение к PostgreSQL успешно"
    }