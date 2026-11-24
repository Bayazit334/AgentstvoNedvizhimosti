
from sqlalchemy import Integer, String
from sqlalchemy.orm import Mapped, mapped_column

from database import Base


class Buyer(Base):
    __tablename__ = "buyers"

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

    passport: Mapped[str] = mapped_column(
        String(255),
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
