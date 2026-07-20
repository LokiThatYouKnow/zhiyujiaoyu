"""
A7 教学实训智能体 - 教师端 LangChain 链
"""

import uuid
import asyncio
from concurrent.futures import ThreadPoolExecutor
from .base import run_chain
from models.prompts import (
    KNOWLEDGE_ORG_SYSTEM, KNOWLEDGE_ORG_HUMAN,
    PRACTISE_DESIGN_SYSTEM, PRACTISE_DESIGN_HUMAN,
    DESIGN_ISSUE_SYSTEM, DESIGN_ISSUE_HUMAN,
    ARRANGE_TIME_SYSTEM, ARRANGE_TIME_HUMAN,
    ISSUE_ANSWER_ANALYSIS_SYSTEM, ISSUE_ANSWER_ANALYSIS_HUMAN,
    COURSEWARE_SEARCH_SYSTEM, COURSEWARE_SEARCH_HUMAN,
    build_prompt,
)
from rag.retriever import retrieve_context, add_course_documents, remove_course_knowledge

# 线程池用于同步阻塞的 DuckDuckGo 搜索
_search_executor = ThreadPoolExecutor(max_workers=3)


async def knowledge_organization(
    course_name: str,
    teacher_username: str,
    resource_name: str = "",
    start_time: str = "",
    user_request: str = "",
) -> dict:
    """知识梳理：基于课程内容生成教学方案"""
    # 从知识库检索课程内容
    context = retrieve_context(course_name, course_name)

    prompt = build_prompt(KNOWLEDGE_ORG_SYSTEM, KNOWLEDGE_ORG_HUMAN)
    result = await run_chain(
        prompt,
        course_name=course_name,
        context=context,
        user_request=user_request or "无特殊需求，请按课程总体内容设计教学方案",
        resource_name=resource_name or "未指定",
    )

    return {"concludeData": result}


async def practise_design(
    course_name: str,
    teacher_username: str,
    resource_name: str = "",
    start_time: str = "",
    user_request: str = "",
) -> dict:
    """实训设计：生成实训项目方案"""
    context = retrieve_context(course_name, course_name)

    prompt = build_prompt(PRACTISE_DESIGN_SYSTEM, PRACTISE_DESIGN_HUMAN)
    result = await run_chain(
        prompt,
        course_name=course_name,
        context=context,
        user_request=user_request or "请设计适合该课程内容的实训项目",
        resource_name=resource_name or "未指定",
    )

    return {"practise": result}


async def design_issue(
    course_name: str,
    teacher_username: str,
    issue_name: str,
    user_request: str = "",
    start_time: str = "",
    end_time: str = "",
) -> dict:
    """智能出题：生成考核题目及答案"""
    context = retrieve_context(course_name, course_name)

    prompt = build_prompt(DESIGN_ISSUE_SYSTEM, DESIGN_ISSUE_HUMAN)
    result = await run_chain(
        prompt,
        course_name=course_name,
        context=context,
        issue_name=issue_name,
        user_request=user_request or "请生成覆盖主要知识点的综合练习题",
        start_time=start_time or "未指定",
        end_time=end_time or "未指定",
    )

    # 生成本地 question_id（替代 COZE 的 question_id）
    question_id = f"q_{uuid.uuid4().hex[:12]}"

    return {
        "decideAndAnswer": result,
        "decideAndAnswerQuestionId": question_id,
    }


async def arrange_time(
    course_name: str,
    teacher_username: str,
) -> dict:
    """时间分配：制定教学时间安排"""
    context = retrieve_context(course_name, course_name)

    prompt = build_prompt(ARRANGE_TIME_SYSTEM, ARRANGE_TIME_HUMAN)
    result = await run_chain(
        prompt,
        course_name=course_name,
        context=context,
    )

    return {"arrangeTime": result}


async def get_issue_answer_detail(
    question_id: str,
    course_name: str,
    teacher_username: str,
) -> dict:
    """答题分析：分析学生答题情况"""
    context = retrieve_context(course_name, course_name)

    prompt = build_prompt(ISSUE_ANSWER_ANALYSIS_SYSTEM, ISSUE_ANSWER_ANALYSIS_HUMAN)
    result = await run_chain(
        prompt,
        course_name=course_name,
        context=context,
        question_id=question_id,
    )

    return {"suggestionsAfterAnalysis": result}


async def search_courseware(
    course_name: str,
    teacher_username: str,
    search_prompt: str = "",
) -> dict:
    """AI 网络搜索课件：使用 DuckDuckGo 搜索网络资源，并由 LLM 整合为结构化课件"""
    # 构建搜索查询
    query = f"{course_name} {search_prompt} 教学课件 课程大纲 知识点"
    search_results_text = ""
    result_count = 0

    # 在线程池中执行同步 DuckDuckGo 搜索
    def _do_search() -> list:
        try:
            from duckduckgo_search import DDGS
            with DDGS() as ddgs:
                return list(ddgs.text(query, max_results=8))
        except Exception:
            return []

    try:
        loop = asyncio.get_event_loop()
        results = await loop.run_in_executor(_search_executor, _do_search)
        if results:
            for i, r in enumerate(results, 1):
                search_results_text += f"\n### 搜索结果 {i}\n"
                search_results_text += f"标题: {r.get('title', '')}\n"
                search_results_text += f"摘要: {r.get('body', '')}\n"
                search_results_text += f"链接: {r.get('href', '')}\n"
            result_count = len(results)
        else:
            search_results_text = "（网络搜索未返回结果，请基于专业知识生成课件）"
    except Exception as e:
        search_results_text = f"（网络搜索遇到问题: {str(e)}。请基于你的专业知识生成课件。）"

    # 使用 LLM 整合搜索结果为结构化课件
    prompt = build_prompt(COURSEWARE_SEARCH_SYSTEM, COURSEWARE_SEARCH_HUMAN)
    organized = await run_chain(
        prompt,
        course_name=course_name,
        search_prompt=search_prompt or "全面搜索课程相关的教学资源、课件大纲和知识点",
        search_results=search_results_text,
    )

    return {
        "coursewareContent": organized,
        "searchQuery": query,
        "resultCount": result_count,
    }


async def add_course_knowledge(
    course_name: str,
    teacher_username: str,
    file_content: str = "",
    file_name: str = "",
) -> dict:
    """添加课程知识库文档"""
    if file_content and file_name:
        result = add_course_documents(course_name, file_content, file_name)
        return {
            "addCourseKnowledgeByAICode": "0" if result["success"] else "1",
            "addCourseKnowledgeByAIDocumentId": (
                course_name if result["success"] else ""
            ),
            "message": result["message"],
            "chunk_count": result["chunk_count"],
        }
    else:
        # 无文件上传时，仅创建空白知识库
        return {
            "addCourseKnowledgeByAICode": "0",
            "addCourseKnowledgeByAIDocumentId": course_name,
            "message": f"已为课程 '{course_name}' 创建知识库（无文档）",
            "chunk_count": 0,
        }


async def delete_course_knowledge(course_name: str, teacher_username: str) -> dict:
    """删除课程知识库"""
    result = remove_course_knowledge(course_name)
    return {
        "teacherCourseListDeleteCourseInfo": (
            "删除成功" if result["success"] else "删除失败"
        ),
    }


async def delete_issue_knowledge(question_id: str) -> dict:
    """删除练习（本地只需确认删除）"""
    return {"teacherCourseListDeleteIssueInfo": "删除成功"}


async def delete_resource_knowledge(resource_id: str) -> dict:
    """删除课件资源（本地只需确认删除）"""
    return {"teacherCourseListDeleteResourceInfo": "删除成功"}


async def get_issue_list(course_name: str, teacher_username: str) -> dict:
    """获取练习列表（从本地数据库查询，此处返回空列表让后端处理）"""
    return {"getTeacherIssueList": "[]"}


async def get_resource_list(course_name: str, teacher_username: str) -> dict:
    """获取课件资源列表（从本地数据库查询）"""
    return {"teacherResourceToGetResourceList": "[]"}
