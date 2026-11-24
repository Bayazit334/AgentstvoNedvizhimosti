from pathlib import Path
from uuid import uuid4

from fastapi import APIRouter, UploadFile, File, HTTPException


router = APIRouter(
    prefix="/documents",
    tags=["Документы"]
)

UPLOAD_DIR = Path("/app/uploads")
UPLOAD_DIR.mkdir(parents=True, exist_ok=True)


@router.post("/upload")
async def upload_document(
    file: UploadFile = File(...)
):
    if not file.filename or not file.filename.lower().endswith(".pdf"):
        raise HTTPException(
            status_code=400,
            detail="Разрешены только PDF-файлы"
        )

    content = await file.read()

    if not content.startswith(b"%PDF-"):
        raise HTTPException(
            status_code=400,
            detail="Файл не является корректным PDF-документом"
        )

    if len(content) > 10 * 1024 * 1024:
        raise HTTPException(
            status_code=400,
            detail="Размер файла не должен превышать 10 МБ"
        )

    filename = f"{uuid4().hex}.pdf"
    file_path = UPLOAD_DIR / filename

    file_path.write_bytes(content)

    return {
        "message": "PDF-документ успешно загружен",
        "file_path": f"uploads/{filename}"
    }