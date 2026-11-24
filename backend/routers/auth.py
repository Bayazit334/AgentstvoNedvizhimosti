
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from pwdlib import PasswordHash

from database import get_db
from schemas import UserRegister, UserLogin
from models import Owner, Buyer, Realtor


router = APIRouter(
    prefix="/auth",
    tags=["Авторизация"]
)

password_hash = PasswordHash.recommended()


def get_password_hash(password: str) -> str:
    return password_hash.hash(password)


def verify_password(
    plain_password: str,
    hashed_password: str
) -> bool:
    return password_hash.verify(
        plain_password,
        hashed_password
    )


@router.post("/register/{role}")
def register_user(
    role: str,
    user: UserRegister,
    db: Session = Depends(get_db)
):
    if role == "owner":
        model = Owner
    elif role == "buyer":
        model = Buyer
    elif role == "realtor":
        model = Realtor
    else:
        raise HTTPException(
            status_code=400,
            detail="Неизвестная роль пользователя"
        )

    existing_user = db.query(model).filter(
        model.login == user.login
    ).first()

    if existing_user:
        raise HTTPException(
            status_code=400,
            detail="Такой логин уже существует"
        )

    new_user = model(
        surname=user.surname,
        first_name=user.first_name,
        patronymic=user.patronymic,
        login=user.login,
        password_hash=get_password_hash(user.password)
    )

    if role in ["owner", "buyer"]:
        new_user.passport = user.passport

    if role == "owner":
        new_user.ownership_document = user.ownership_document

    db.add(new_user)
    db.commit()
    db.refresh(new_user)

    return {
        "message": "Пользователь успешно зарегистрирован",
        "user_id": new_user.id,
        "role": role
    }


@router.post("/login/{role}")
def login_user(
    role: str,
    user: UserLogin,
    db: Session = Depends(get_db)
):
    if role == "owner":
        model = Owner
    elif role == "buyer":
        model = Buyer
    elif role == "realtor":
        model = Realtor
    else:
        raise HTTPException(
            status_code=400,
            detail="Неизвестная роль пользователя"
        )

    existing_user = db.query(model).filter(
        model.login == user.login
    ).first()

    if not existing_user:
        raise HTTPException(
            status_code=401,
            detail="Неверный логин или пароль"
        )

    if not verify_password(
        user.password,
        existing_user.password_hash
    ):
        raise HTTPException(
            status_code=401,
            detail="Неверный логин или пароль"
        )

    return {
        "message": "Авторизация успешна",
        "user_id": existing_user.id,
        "role": role
    }
