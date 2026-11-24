
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from database import get_db
from models import Property, Owner, Realtor
from schemas import PropertyCreate, PropertySearch


router = APIRouter(
    prefix="/properties",
    tags=["Недвижимость"]
)


@router.get("/owners")
def get_owners(
    db: Session = Depends(get_db)
):
    owners = db.query(Owner).order_by(Owner.id).all()

    return [
        {
            "id": owner.id,
            "surname": owner.surname,
            "first_name": owner.first_name,
            "patronymic": owner.patronymic
        }
        for owner in owners
    ]


@router.get("/realtors")
def get_realtors(
    db: Session = Depends(get_db)
):
    realtors = db.query(Realtor).order_by(Realtor.id).all()

    return [
        {
            "id": realtor.id,
            "surname": realtor.surname,
            "first_name": realtor.first_name,
            "patronymic": realtor.patronymic
        }
        for realtor in realtors
    ]


@router.post("/")
def create_property(
    property_data: PropertyCreate,
    db: Session = Depends(get_db)
):
    owner = db.query(Owner).filter(
        Owner.id == property_data.owner_id
    ).first()

    if not owner:
        from fastapi import HTTPException
        raise HTTPException(
            status_code=404,
            detail="Собственник не найден"
        )

    realtor = db.query(Realtor).filter(
        Realtor.id == property_data.realtor_id
    ).first()

    if not realtor:
        from fastapi import HTTPException
        raise HTTPException(
            status_code=404,
            detail="Риелтор не найден"
        )

    new_property = Property(
        property_type=property_data.property_type,
        address=property_data.address,
        district=property_data.district,
        area=property_data.area,
        rooms=property_data.rooms,
        price=property_data.price,
        owner_id=property_data.owner_id,
        realtor_id=property_data.realtor_id,
        document=property_data.document
    )

    db.add(new_property)
    db.commit()
    db.refresh(new_property)

    return {
        "message": "Объект недвижимости успешно добавлен",
        "property_id": new_property.id
    }


@router.get("/search")
def search_properties(
    filters: PropertySearch = Depends(),
    db: Session = Depends(get_db)
):
    query = db.query(Property)

    if filters.property_type:
        query = query.filter(
            Property.property_type.ilike(
                f"%{filters.property_type}%"
            )
        )

    if filters.min_price is not None:
        query = query.filter(
            Property.price >= filters.min_price
        )

    if filters.max_price is not None:
        query = query.filter(
            Property.price <= filters.max_price
        )

    if filters.district:
        query = query.filter(
            Property.district.ilike(
                f"%{filters.district}%"
            )
        )

    if filters.rooms is not None:
        query = query.filter(
            Property.rooms == filters.rooms
        )

    properties = query.all()

    return [
        {
            "id": property.id,
            "property_type": property.property_type,
            "address": property.address,
            "district": property.district,
            "area": float(property.area),
            "rooms": property.rooms,
            "price": float(property.price),
            "owner_id": property.owner_id,
            "realtor_id": property.realtor_id,
            "document": property.document
        }
        for property in properties
    ]
