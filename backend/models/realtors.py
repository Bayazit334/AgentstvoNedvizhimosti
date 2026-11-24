
from sqlalchemy import Integer, String
from sqlalchemy.orm import Mapped, mapped_column

from database import Base


class Realtor(Base):
    __tablename__ = "realtors"

    id: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    surname: Mapped[str] = mapped_column(
        String(100),
        nullable=False
    )

    first_name: Mapped[str] = mapped_column(
        String(100),
        nullable=False
    )

    patronymic: Mapped[str] = mapped_column(
        String(100),
        nullable=False
    )

    login: Mapped[str] = mapped_column(
        String(100),
        unique=True,
        nullable=False
    )

    password_hash: Mapped[str] = mapped_column(
        String(255),
        nullable=False
    )
