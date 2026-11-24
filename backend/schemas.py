
from datetime import date

from pydantic import BaseModel


class UserRegister(BaseModel):
    surname: str
    first_name: str
    patronymic: str
    passport: str | None = None
    ownership_document: str | None = None
    login: str
    password: str


class UserLogin(BaseModel):
    login: str
    password: str


class PropertyCreate(BaseModel):
    property_type: str
    address: str
    district: str
    area: float
    rooms: int
    price: float
    owner_id: int
    realtor_id: int
    document: str


class PropertySearch(BaseModel):
    property_type: str | None = None
    min_price: float | None = None
    max_price: float | None = None
    district: str | None = None
    rooms: int | None = None


class DealCreate(BaseModel):
    property_id: int
    buyer_id: int
    owner_id: int
    realtor_id: int
    deal_date: date
    deal_amount: float
