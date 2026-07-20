"""
A7 教学实训智能体 - 文档加载器
支持 PDF、DOCX、TXT、Markdown 格式
"""

import base64
import os
import tempfile
from pathlib import Path
from langchain_core.documents import Document
from langchain_text_splitters import RecursiveCharacterTextSplitter
from config import CHUNK_SIZE, CHUNK_OVERLAP, ALLOWED_EXTENSIONS, KNOWLEDGE_BASE_DIR


def _get_text_splitter() -> RecursiveCharacterTextSplitter:
    """获取文本分块器"""
    return RecursiveCharacterTextSplitter(
        chunk_size=CHUNK_SIZE,
        chunk_overlap=CHUNK_OVERLAP,
        separators=["\n\n", "\n", "。", "！", "？", "；", "，", " ", ""],
        length_function=len,
    )


def load_text_file(file_path: str) -> list[Document]:
    """加载文本文件（TXT 或 Markdown）"""
    with open(file_path, "r", encoding="utf-8") as f:
        text = f.read()

    splitter = _get_text_splitter()
    chunks = splitter.create_documents(
        texts=[text],
        metadatas=[{"source": os.path.basename(file_path)}],
    )
    return chunks


def load_pdf_file(file_path: str) -> list[Document]:
    """加载 PDF 文件"""
    try:
        from pypdf import PdfReader
        reader = PdfReader(file_path)
        text_parts = []
        for i, page in enumerate(reader.pages):
            page_text = page.extract_text()
            if page_text:
                text_parts.append(page_text)

        text = "\n\n".join(text_parts)
        if not text.strip():
            raise ValueError("PDF 文件中未提取到文本内容")

        splitter = _get_text_splitter()
        chunks = splitter.create_documents(
            texts=[text],
            metadatas=[{"source": os.path.basename(file_path)}],
        )
        return chunks
    except ImportError:
        raise ImportError("需要安装 pypdf 库：pip install pypdf")


def load_docx_file(file_path: str) -> list[Document]:
    """加载 DOCX 文件"""
    try:
        from docx import Document as DocxDocument
        doc = DocxDocument(file_path)
        text_parts = []
        for para in doc.paragraphs:
            if para.text.strip():
                text_parts.append(para.text)

        text = "\n\n".join(text_parts)
        if not text.strip():
            raise ValueError("DOCX 文件中未提取到文本内容")

        splitter = _get_text_splitter()
        chunks = splitter.create_documents(
            texts=[text],
            metadatas=[{"source": os.path.basename(file_path)}],
        )
        return chunks
    except ImportError:
        raise ImportError("需要安装 python-docx 库：pip install python-docx")


def load_file(file_path: str) -> list[Document]:
    """根据扩展名自动选择加载器"""
    ext = Path(file_path).suffix.lower()
    if ext not in ALLOWED_EXTENSIONS:
        raise ValueError(f"不支持的文件格式: {ext}，支持的格式: {ALLOWED_EXTENSIONS}")

    if ext == ".pdf":
        return load_pdf_file(file_path)
    elif ext == ".docx":
        return load_docx_file(file_path)
    elif ext in (".txt", ".md"):
        return load_text_file(file_path)
    else:
        raise ValueError(f"未知文件格式: {ext}")


def load_file_from_base64(
    file_base64: str,
    file_name: str,
    save_to_disk: bool = True
) -> list[Document]:
    """从 Base64 编码的字符串加载文档"""
    file_bytes = base64.b64decode(file_base64)
    ext = Path(file_name).suffix.lower()

    if ext not in ALLOWED_EXTENSIONS:
        raise ValueError(f"不支持的文件格式: {ext}")

    # 保存到临时文件
    with tempfile.NamedTemporaryFile(delete=False, suffix=ext) as tmp:
        tmp.write(file_bytes)
        tmp_path = tmp.name

    try:
        documents = load_file(tmp_path)

        # 更新元数据
        for doc in documents:
            doc.metadata["source"] = file_name

        # 持久化保存
        if save_to_disk:
            persistent_dir = os.path.join(KNOWLEDGE_BASE_DIR, "courses")
            os.makedirs(persistent_dir, exist_ok=True)
            dest_path = os.path.join(persistent_dir, file_name)
            with open(dest_path, "wb") as f:
                f.write(file_bytes)

        return documents
    finally:
        os.unlink(tmp_path)


def load_text_from_string(text: str, source_name: str = "inline") -> list[Document]:
    """从纯文本字符串创建文档块"""
    splitter = _get_text_splitter()
    chunks = splitter.create_documents(
        texts=[text],
        metadatas=[{"source": source_name}],
    )
    return chunks
