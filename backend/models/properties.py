from sqlalchemy import ForeignKey, Integer, Numeric, String
from sqlalchemy.orm import Mapped, mapped_column

from database import Base


class Property(Base):
    __tablename__ = "properties"

    id: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    property_type: Mapped[str] = mapped_column(
        String(100),
        nullable=False
    )

    address: Mapped[str] = mapped_column(
        String(500),
        nullable=False
    )

    district: Mapped[str] = mapped_column(
        String(100),
        nullable=False,
        default="",
        server_default=""
    )

    area: Mapped[float] = mapped_column(
        Numeric(10, 2),
        nullable=False
    )

    rooms: Mapped[int] = mapped_column(
        Integer,
        nullable=False
    )

    price: Mapped[float] = mapped_column(
        Numeric(12, 2),
        nullable=False
    )

    owner_id: Mapped[int] = mapped_column(
        ForeignKey("owners.id"),
        nullable=False
    )

    realtor_id: Mapped[int] = mapped_column(
        ForeignKey("realtors.id"),
        nullable=False
    )

    document: Mapped[str] = mapped_column(
        String(500),
        nullable=False
    )