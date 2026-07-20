"""
A7 教学实训智能体 - ChromaDB 向量存储管理
"""

import hashlib
import os
import shutil
from langchain_chroma import Chroma
from langchain_core.documents import Document
from .embeddings import get_embeddings
from config import CHROMA_PERSIST_DIR


def _sanitize_collection_name(name: str) -> str:
    """
    将任意课程名称转换为合法的 ChromaDB collection name。

    ChromaDB 要求: 3-63 字符，首尾字母数字，仅含 [a-zA-Z0-9_-]，
    不含连续句点，不能是 IPv4 地址。中文等非 ASCII 字符会导致创建失败。
    """
    # 提取可读前缀：保留 ASCII 字母数字，其余替换为下划线
    ascii_prefix = ''.join(c if c.isascii() and c.isalnum() else '_' for c in name)
    ascii_prefix = ascii_prefix.strip('_')[:20].strip('_')

    # 用 hash 保证唯一性和稳定性
    hash_hex = hashlib.md5(name.encode('utf-8')).hexdigest()[:16]

    if ascii_prefix:
        return f"{ascii_prefix}_{hash_hex}"
    else:
        return f"kb_{hash_hex}"


def _get_collection_path(collection_name: str) -> str:
    """获取 collection 持久化路径（使用安全名称）"""
    safe_name = _sanitize_collection_name(collection_name)
    return os.path.join(CHROMA_PERSIST_DIR, safe_name)


def get_vector_store(collection_name: str) -> Chroma:
    """获取或创建 ChromaDB collection"""
    safe_name = _sanitize_collection_name(collection_name)
    persist_dir = os.path.join(CHROMA_PERSIST_DIR, safe_name)
    os.makedirs(persist_dir, exist_ok=True)

    return Chroma(
        collection_name=safe_name,
        embedding_function=get_embeddings(),
        persist_directory=persist_dir,
    )


def add_documents(collection_name: str, documents: list[Document]) -> int:
    """向知识库添加文档块，返回添加的块数"""
    if not documents:
        return 0
    vector_store = get_vector_store(collection_name)
    vector_store.add_documents(documents)
    return len(documents)


def similarity_search(collection_name: str, query: str, k: int = 6) -> list[Document]:
    """相似度检索"""
    vector_store = get_vector_store(collection_name)
    return vector_store.similarity_search(query, k=k)


def delete_collection(collection_name: str) -> bool:
    """删除整个 collection"""
    try:
        vector_store = get_vector_store(collection_name)
        vector_store.delete_collection()
        # 删除持久化目录
        persist_dir = _get_collection_path(collection_name)
        if os.path.exists(persist_dir):
            shutil.rmtree(persist_dir)
        return True
    except Exception:
        return False


def collection_exists(collection_name: str) -> bool:
    """检查 collection 是否存在"""
    persist_dir = _get_collection_path(collection_name)
    return os.path.exists(persist_dir) and os.path.isdir(persist_dir)


def get_collection_stats(collection_name: str) -> dict:
    """获取 collection 统计信息"""
    if not collection_exists(collection_name):
        return {"exists": False, "document_count": 0}
    try:
        vector_store = get_vector_store(collection_name)
        count = vector_store._collection.count()
        return {"exists": True, "document_count": count}
    except Exception:
        return {"exists": False, "document_count": 0}


def list_all_collections() -> list[str]:
    """列出所有知识库 collection"""
    if not os.path.exists(CHROMA_PERSIST_DIR):
        return []
    return [d for d in os.listdir(CHROMA_PERSIST_DIR)
            if os.path.isdir(os.path.join(CHROMA_PERSIST_DIR, d))]
