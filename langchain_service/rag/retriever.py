"""
A7 教学实训智能体 - RAG 检索器
提供统一的知识库检索接口
"""

from langchain_core.documents import Document
from .vector_store import similarity_search, get_vector_store, collection_exists
from .document_loader import load_file_from_base64, load_text_from_string, load_file
from config import MAX_RETRIEVED_DOCS


def retrieve_context(course_name: str, query: str, k: int = MAX_RETRIEVED_DOCS) -> str:
    """
    从课程知识库检索相关上下文

    Args:
        course_name: 课程名称 (也是 ChromaDB collection 名称)
        query: 查询文本
        k: 返回的文档块数量

    Returns:
        拼接的上下文字符串，如果知识库为空则返回提示信息
    """
    if not collection_exists(course_name):
        return f"（该课程 '{course_name}' 暂无知识库资料，请基于常识和专业知识回答）"

    docs = similarity_search(course_name, query, k=k)

    if not docs:
        return "（未检索到高度相关的内容，请基于常识和专业知识回答）"

    context_parts = []
    for i, doc in enumerate(docs, 1):
        source = doc.metadata.get("source", "未知来源")
        context_parts.append(f"[参考资料 {i} | 来源: {source}]\n{doc.page_content}\n")

    return "\n".join(context_parts)


def add_course_documents(course_name: str, file_base64: str, file_name: str) -> dict:
    """
    向课程知识库添加文档

    Returns:
        {"success": bool, "chunk_count": int, "message": str}
    """
    try:
        documents = load_file_from_base64(file_base64, file_name, save_to_disk=True)

        if not documents:
            return {"success": False, "chunk_count": 0, "message": "文档内容为空"}

        # 为每个文档块添加课程元数据
        for doc in documents:
            doc.metadata["course_name"] = course_name

        from .vector_store import add_documents
        chunk_count = add_documents(course_name, documents)

        return {
            "success": True,
            "chunk_count": chunk_count,
            "message": f"成功添加 {chunk_count} 个文档块到课程 '{course_name}' 的知识库",
        }
    except Exception as e:
        return {"success": False, "chunk_count": 0, "message": str(e)}


def remove_course_knowledge(course_name: str) -> dict:
    """删除课程的所有知识库文档"""
    from .vector_store import delete_collection
    success = delete_collection(course_name)
    if success:
        return {"success": True, "message": f"课程 '{course_name}' 的知识库已删除"}
    else:
        return {"success": False, "message": f"删除课程 '{course_name}' 的知识库失败"}
