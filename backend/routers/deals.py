
from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session

from database import get_db
from models import Deal, Property, Buyer, Owner
from schemas import DealCreate


router = APIRouter(
    prefix="/deals",
    tags=["Сделки"]
)


@router.get("/properties")
def get_deal_properties(
    db: Session = Depends(get_db)
):
    properties = db.query(Property).all()

    return [
        {
            "id": property.id,
            "property_type": property.property_type,
            "address": property.address,
            "price": float(property.price),
            "owner_id": property.owner_id,
            "realtor_id": property.realtor_id
        }
        for property in properties
    ]


@router.get("/buyers")
def get_deal_buyers(
    db: Session = Depends(get_db)
):
    buyers = db.query(Buyer).all()

    return [
        {
            "id": buyer.id,
            "surname": buyer.surname,
            "first_name": buyer.first_name,
            "patronymic": buyer.patronymic
        }
        for buyer in buyers
    ]


@router.get("/owners")
def get_deal_owners(
    db: Session = Depends(get_db)
):
    owners = db.query(Owner).all()

    return [
        {
            "id": owner.id,
            "surname": owner.surname,
            "first_name": owner.first_name,
            "patronymic": owner.patronymic
        }
        for owner in owners
    ]


@router.post("/")
def create_deal(
    deal_data: DealCreate,
    db: Session = Depends(get_db)
):
    new_deal = Deal(
        property_id=deal_data.property_id,
        buyer_id=deal_data.buyer_id,
        owner_id=deal_data.owner_id,
        realtor_id=deal_data.realtor_id,
        deal_date=deal_data.deal_date,
        deal_amount=deal_data.deal_amount
    )

    db.add(new_deal)
    db.commit()
    db.refresh(new_deal)

    return {
        "message": "Сделка успешно зарегистрирована",
        "deal_id": new_deal.id
    }


@router.get("/")
def get_deals(
    realtor_id: int | None = Query(default=None),
    db: Session = Depends(get_db)
):
    query = db.query(Deal)

    if realtor_id is not None:
        query = query.filter(Deal.realtor_id == realtor_id)

    deals = query.order_by(Deal.id.desc()).all()

    result = []

    for deal in deals:
        property = db.query(Property).filter(
            Property.id == deal.property_id
        ).first()

        buyer = db.query(Buyer).filter(
            Buyer.id == deal.buyer_id
        ).first()

        owner = db.query(Owner).filter(
            Owner.id == deal.owner_id
        ).first()

        result.append({
            "id": deal.id,
            "property_id": deal.property_id,
            "property_type": property.property_type if property else "",
            "property_address": property.address if property else "",
            "buyer_id": deal.buyer_id,
            "buyer_name": (
                f"{buyer.surname} {buyer.first_name} {buyer.patronymic}"
                if buyer else ""
            ),
            "owner_id": deal.owner_id,
            "owner_name": (
                f"{owner.surname} {owner.first_name} {owner.patronymic}"
                if owner else ""
            ),
            "realtor_id": deal.realtor_id,
            "deal_date": str(deal.deal_date),
            "deal_amount": float(deal.deal_amount)
        })

    return result
