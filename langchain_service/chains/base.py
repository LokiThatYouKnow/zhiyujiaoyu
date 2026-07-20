"""
A7 教学实训智能体 - LLM 链基类
使用 DeepSeek Cloud API（兼容 OpenAI 格式）
"""

from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from config import (
    DEEPSEEK_API_KEY, DEEPSEEK_BASE_URL, DEEPSEEK_MODEL,
    LLM_TEMPERATURE, LLM_TOP_P, LLM_MAX_TOKENS, LLM_TIMEOUT,
)

# 全局 LLM 单例
_llm: ChatOpenAI | None = None


def get_llm() -> ChatOpenAI:
    """获取 DeepSeek ChatOpenAI 实例（单例）"""
    global _llm
    if _llm is None:
        _llm = ChatOpenAI(
            base_url=DEEPSEEK_BASE_URL,
            api_key=DEEPSEEK_API_KEY,
            model=DEEPSEEK_MODEL,
            temperature=LLM_TEMPERATURE,
            top_p=LLM_TOP_P,
            max_tokens=LLM_MAX_TOKENS,
            timeout=LLM_TIMEOUT,
        )
    return _llm


# LLM 实例缓存（按模型 ID 区分）
_chat_llms: dict[str, ChatOpenAI] = {}


def get_chat_llm(model_id: str | None = None) -> ChatOpenAI:
    """
    获取指定模型的 ChatOpenAI 实例（按模型缓存）
    用于通用聊天场景，temperature 稍高以获得更自然的对话
    """
    model = model_id or DEEPSEEK_MODEL
    if model not in _chat_llms:
        _chat_llms[model] = ChatOpenAI(
            base_url=DEEPSEEK_BASE_URL,
            api_key=DEEPSEEK_API_KEY,
            model=model,
            temperature=0.7,  # 聊天场景更高的温度
            top_p=LLM_TOP_P,
            max_tokens=LLM_MAX_TOKENS,
            timeout=LLM_TIMEOUT,
        )
    return _chat_llms[model]


async def run_chain(prompt: ChatPromptTemplate, **kwargs) -> str:
    """
    执行 LangChain Chain 并返回文本结果

    Args:
        prompt: ChatPromptTemplate 实例
        **kwargs: 模板变量

    Returns:
        LLM 生成的文本
    """
    llm = get_llm()
    chain = prompt | llm | StrOutputParser()
    result = await chain.ainvoke(kwargs)
    return result
