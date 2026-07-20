"""
A7 教学实训智能体 - Embeddings 模型封装
使用 Ollama 本地 Embedding 模型（轻量，无需 GPU）
"""

from langchain_ollama import OllamaEmbeddings
from config import OLLAMA_BASE_URL, EMBEDDING_MODEL

# 全局单例
_embeddings: OllamaEmbeddings | None = None


def get_embeddings() -> OllamaEmbeddings:
    """获取 Ollama Embeddings 实例（单例）"""
    global _embeddings
    if _embeddings is None:
        _embeddings = OllamaEmbeddings(
            base_url=OLLAMA_BASE_URL,
            model=EMBEDDING_MODEL,
        )
    return _embeddings
