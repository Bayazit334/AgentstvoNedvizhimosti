
"""Initial creation of five tables."""

from alembic import op
import sqlalchemy as sa


revision = "82168dbf2c92"
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    op.create_table(
        "owners",
        sa.Column("id", sa.Integer(), autoincrement=True, nullable=False),
        sa.Column("surname", sa.String(length=100), nullable=False),
        sa.Column("first_name", sa.String(length=100), nullable=False),
        sa.Column("patronymic", sa.String(length=100), nullable=False),
        sa.Column("passport", sa.String(length=255), nullable=False),
        sa.Column("ownership_document", sa.String(length=500), nullable=False),
        sa.Column("login", sa.String(length=100), nullable=False),
        sa.Column("password_hash", sa.String(length=255), nullable=False),
        sa.PrimaryKeyConstraint("id"),
        sa.UniqueConstraint("login")
    )

    op.create_table(
        "buyers",
        sa.Column("id", sa.Integer(), autoincrement=True, nullable=False),
        sa.Column("surname", sa.String(length=100), nullable=False),
        sa.Column("first_name", sa.String(length=100), nullable=False),
        sa.Column("patronymic", sa.String(length=100), nullable=False),
        sa.Column("passport", sa.String(length=255), nullable=False),
        sa.Column("login", sa.String(length=100), nullable=False),
        sa.Column("password_hash", sa.String(length=255), nullable=False),
        sa.PrimaryKeyConstraint("id"),
        sa.UniqueConstraint("login")
    )

    op.create_table(
        "realtors",
        sa.Column("id", sa.Integer(), autoincrement=True, nullable=False),
        sa.Column("surname", sa.String(length=100), nullable=False),
        sa.Column("first_name", sa.String(length=100), nullable=False),
        sa.Column("patronymic", sa.String(length=100), nullable=False),
        sa.Column("login", sa.String(length=100), nullable=False),
        sa.Column("password_hash", sa.String(length=255), nullable=False),
        sa.PrimaryKeyConstraint("id"),
        sa.UniqueConstraint("login")
    )

    op.create_table(
        "properties",
        sa.Column("id", sa.Integer(), autoincrement=True, nullable=False),
        sa.Column("property_type", sa.String(length=100), nullable=False),
        sa.Column("address", sa.String(length=500), nullable=False),
        sa.Column(
            "district",
            sa.String(length=100),
            nullable=False,
            server_default=""
        ),
        sa.Column("area", sa.Numeric(precision=10, scale=2), nullable=False),
        sa.Column("rooms", sa.Integer(), nullable=False),
        sa.Column("price", sa.Numeric(precision=12, scale=2), nullable=False),
        sa.Column("owner_id", sa.Integer(), nullable=False),
        sa.Column("realtor_id", sa.Integer(), nullable=False),
        sa.Column("document", sa.String(length=500), nullable=False),
        sa.ForeignKeyConstraint(["owner_id"], ["owners.id"]),
        sa.ForeignKeyConstraint(["realtor_id"], ["realtors.id"]),
        sa.PrimaryKeyConstraint("id")
    )

    op.create_table(
        "deals",
        sa.Column("id", sa.Integer(), autoincrement=True, nullable=False),
        sa.Column("property_id", sa.Integer(), nullable=False),
        sa.Column("buyer_id", sa.Integer(), nullable=False),
        sa.Column("owner_id", sa.Integer(), nullable=False),
        sa.Column("realtor_id", sa.Integer(), nullable=False),
        sa.Column("deal_date", sa.Date(), nullable=False),
        sa.Column("deal_amount", sa.Numeric(precision=12, scale=2), nullable=False),
        sa.ForeignKeyConstraint(["property_id"], ["properties.id"]),
        sa.ForeignKeyConstraint(["buyer_id"], ["buyers.id"]),
        sa.ForeignKeyConstraint(["owner_id"], ["owners.id"]),
        sa.ForeignKeyConstraint(["realtor_id"], ["realtors.id"]),
        sa.PrimaryKeyConstraint("id")
    )


def downgrade():
    op.drop_table("deals")
    op.drop_table("properties")
    op.drop_table("realtors")
    op.drop_table("buyers")
    op.drop_table("owners")
