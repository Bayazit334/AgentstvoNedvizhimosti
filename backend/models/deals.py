
from datetime import date
from decimal import Decimal

from sqlalchemy import Date, ForeignKey, Integer, Numeric
from sqlalchemy.orm import Mapped, mapped_column

from database import Base


class Deal(Base):
    __tablename__ = "deals"

    id: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True
    )

    property_id: Mapped[int] = mapped_column(
        ForeignKey("properties.id"),
        nullable=False
    )

    buyer_id: Mapped[int] = mapped_column(
        ForeignKey("buyers.id"),
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

    deal_date: Mapped[date] = mapped_column(
        Date,
        nullable=False
    )

    deal_amount: Mapped[Decimal] = mapped_column(
        Numeric(12, 2),
        nullable=False
    )
