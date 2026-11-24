from pathlib import Path

content = [
    b"%PDF-1.4\n",
    b"1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n",
    b"2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n",
    b"3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 300 200] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>\nendobj\n",
    b"4 0 obj\n<< /Length 44 >>\nstream\nBT /F1 18 Tf 40 100 Td (Test PDF document) Tj ET\nendstream\nendobj\n",
    b"5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n",
]

pdf = b""
offsets = [0]

for obj in content:
    offsets.append(len(pdf))
    pdf += obj

xref_offset = len(pdf)

pdf += b"xref\n0 6\n"
pdf += b"0000000000 65535 f \n"

for offset in offsets[1:]:
    pdf += f"{offset:010d} 00000 n \n".encode()

pdf += (
    b"trailer\n<< /Size 6 /Root 1 0 R >>\n"
    + f"startxref\n{xref_offset}\n".encode()
    + b"%%EOF\n"
)

Path("test_document.pdf").write_bytes(pdf)

print("Тестовый PDF успешно создан")
