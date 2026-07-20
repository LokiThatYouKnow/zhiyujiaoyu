"""
A7 教学实训智能体 - FastAPI 主入口
提供所有 AI 功能的 REST API 端点
"""

import os
from pathlib import Path

# 自动加载 .env 文件（如果存在）
_env_path = Path(__file__).parent / ".env"
if _env_path.exists():
    with open(_env_path, "r", encoding="utf-8") as _f:
        for _line in _f:
            _line = _line.strip()
            if _line and not _line.startswith("#") and "=" in _line:
                _key, _, _val = _line.partition("=")
                _key, _val = _key.strip(), _val.strip().strip('"').strip("'")
                if _key not in os.environ:
                    os.environ[_key] = _val

from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from models.schemas import (
    HealthResponse, ErrorResponse,
    KnowledgeOrganizationRequest, PractiseDesignRequest, DesignIssueRequest,
    ArrangeTimeRequest, GetIssueAnswerDetailRequest,
    IssueListRequest, ResourceListRequest,
    DeleteIssueRequest, DeleteResourceRequest, DeleteCourseRequest,
    AnswerRequest, SelfPractiseRequest, SelfPractiseCorrectRequest,
    CoursePractiseRequest, CoursePractiseCorrectAnswerRequest,
    SearchCoursewareRequest,
    ChatRequest,
)
from chains.teacher_chains import (
    knowledge_organization, practise_design, design_issue, arrange_time,
    get_issue_answer_detail, add_course_knowledge, delete_course_knowledge,
    delete_issue_knowledge, delete_resource_knowledge,
    get_issue_list, get_resource_list, search_courseware,
)
from chains.student_chains import (
    student_answer, self_practise, self_practise_correct,
    course_practise, course_practise_correct_answer,
)
from chains.base import get_llm, get_chat_llm
from rag.embeddings import get_embeddings
from rag.vector_store import list_all_collections, collection_exists, get_collection_stats
from config import DEEPSEEK_MODEL, EMBEDDING_MODEL, AVAILABLE_MODELS


@asynccontextmanager
async def lifespan(app: FastAPI):
    """应用生命周期管理"""
    print(f"[INFO] A7 LangChain Service Starting...")
    print(f"   LLM: {DEEPSEEK_MODEL}")
    print(f"   Embedding: {EMBEDDING_MODEL}")
    print(f"   Knowledge Bases: {list_all_collections()}")
    yield
    print("[INFO] A7 LangChain Service Shutdown")


app = FastAPI(
    title="A7 教学实训智能体 - LangChain AI Service",
    description="基于 Qwen3 + Ollama + ChromaDB 的智能教学服务",
    version="2.0.0",
    lifespan=lifespan,
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ===== 辅助函数 =====

def success_response(data: object) -> dict:
    """构建标准成功响应"""
    return {"code": 200, "msg": "success", "data": data}


def error_response(msg: str, code: int = 400) -> dict:
    """构建标准错误响应"""
    return {"code": code, "msg": msg, "data": None}


def make_response(data: dict) -> dict:
    """将 dict 包装成 Spring Boot R 格式：data 是包含一个 Map 的 List"""
    return success_response([data])


# ===== 健康检查 =====

@app.get("/api/langchain/health")
async def health_check():
    """检查服务状态"""
    deepseek_ok = False
    chroma_ok = False
    try:
        llm = get_llm()
        await llm.ainvoke("ping")
        deepseek_ok = True
    except Exception:
        deepseek_ok = False

    try:
        embeddings = get_embeddings()
        _ = embeddings.embed_query("test")
        chroma_ok = True
    except Exception:
        chroma_ok = False

    return HealthResponse(
        status="ok" if (deepseek_ok and chroma_ok) else "degraded",
        llm_model=DEEPSEEK_MODEL,
        embedding_model=EMBEDDING_MODEL,
        deepseek_connected=deepseek_ok,
        chroma_ok=chroma_ok,
    )


# ===== 知识库管理端点 =====

@app.get("/api/langchain/knowledge/collections")
async def list_collections():
    """列出所有知识库"""
    collections = list_all_collections()
    stats = {c: get_collection_stats(c) for c in collections}
    return success_response({"collections": collections, "stats": stats})


# ===== 教师端 AI 端点 =====

@app.post("/api/langchain/teacher/knowledgeOrganization")
async def api_knowledge_organization(req: KnowledgeOrganizationRequest):
    """知识梳理：生成教学方案"""
    try:
        result = await knowledge_organization(
            course_name=req.data,
            teacher_username=req.teacher_username,
            resource_name=req.resource_name,
            start_time=req.start_time,
            user_request=req.input,
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/teacher/practiseDesign")
async def api_practise_design(req: PractiseDesignRequest):
    """实训设计：生成实训项目方案"""
    try:
        result = await practise_design(
            course_name=req.data,
            teacher_username=req.teacher_username,
            resource_name=req.resource_name,
            start_time=req.start_time,
            user_request=req.input,
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/teacher/designIssue")
async def api_design_issue(req: DesignIssueRequest):
    """智能出题：生成考核题目"""
    try:
        result = await design_issue(
            course_name=req.data,
            teacher_username=req.teacher_username,
            issue_name=req.issue_name,
            user_request=req.input,
            start_time=req.start_time,
            end_time=req.end_time,
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/teacher/arrangeTime")
async def api_arrange_time(req: ArrangeTimeRequest):
    """时间分配"""
    try:
        result = await arrange_time(
            course_name=req.data,
            teacher_username=req.teacher_username,
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/teacher/searchCourseware")
async def api_search_courseware(req: SearchCoursewareRequest):
    """AI 网络搜索课件：教师提供搜索提示词，系统搜索网络资源并整合为结构化课件"""
    try:
        result = await search_courseware(
            course_name=req.course_name,
            teacher_username=req.teacher_username,
            search_prompt=req.search_prompt,
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/teacher/getIssueAnswerDetail")
async def api_get_issue_answer_detail(req: GetIssueAnswerDetailRequest):
    """答题分析"""
    try:
        result = await get_issue_answer_detail(
            question_id=req.question_id,
            course_name=req.data,
            teacher_username=req.teacher_username,
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/teacher/addCourseKnowledge")
async def api_add_course_knowledge(req: dict):
    """添加课程知识库文档"""
    try:
        result = await add_course_knowledge(
            course_name=req.get("data", ""),
            teacher_username=req.get("teacher_username", ""),
            file_content=req.get("file_base64", ""),
            file_name=req.get("file_name", ""),
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/teacher/deleteCourse")
async def api_delete_course(req: DeleteCourseRequest):
    """删除课程"""
    try:
        result = await delete_course_knowledge(
            course_name=req.data,
            teacher_username=req.teacher_username,
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/teacher/deleteIssue")
async def api_delete_issue(req: DeleteIssueRequest):
    """删除练习"""
    try:
        result = await delete_issue_knowledge(question_id=req.question_id)
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/teacher/deleteResource")
async def api_delete_resource(req: DeleteResourceRequest):
    """删除资源"""
    try:
        result = await delete_resource_knowledge(resource_id=req.resource_id)
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/teacher/getIssueList")
async def api_get_issue_list(req: IssueListRequest):
    """获取练习列表"""
    try:
        result = await get_issue_list(
            course_name=req.data,
            teacher_username=req.teacher_username,
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/teacher/getResourceList")
async def api_get_resource_list(req: ResourceListRequest):
    """获取课件资源列表"""
    try:
        result = await get_resource_list(
            course_name=req.data,
            teacher_username=req.teacher_username,
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# ===== 学生端 AI 端点 =====

@app.post("/api/langchain/student/answer")
async def api_student_answer(req: AnswerRequest):
    """AI 问答"""
    try:
        result = await student_answer(
            course_name=req.data,
            question=req.input,
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/student/selfPractise")
async def api_self_practise(req: SelfPractiseRequest):
    """自主练习"""
    try:
        result = await self_practise(
            course_name=req.data,
            student_username=req.student_username,
            teacher_username=req.teacher_username,
            user_request=req.input,
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/student/selfPractiseCorrect")
async def api_self_practise_correct(req: SelfPractiseCorrectRequest):
    """练习纠错"""
    try:
        result = await self_practise_correct(
            course_name=req.data,
            student_username=req.student_username,
            teacher_username=req.teacher_username,
            student_answer=req.input,
            question_id=req.question_id,
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/student/coursePractise")
async def api_course_practise(req: CoursePractiseRequest):
    """课程练习"""
    try:
        result = await course_practise(
            course_name=req.data,
            teacher_username=req.teacher_username,
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/langchain/student/coursePractiseCorrectAnswer")
async def api_course_practise_correct_answer(req: CoursePractiseCorrectAnswerRequest):
    """答案对比"""
    try:
        result = await course_practise_correct_answer(
            course_name=req.data,
            student_username=req.student_username,
            teacher_username=req.teacher_username,
            student_answer=req.input,
            question_id=req.question_id,
        )
        return make_response(result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# ===== 通用聊天端点 =====

@app.get("/api/langchain/models")
async def list_models():
    """获取可用模型列表及其在线状态"""
    models_status = []
    for m in AVAILABLE_MODELS:
        # 检测模型是否在线：尝试简单调用
        online = False
        try:
            test_llm = get_chat_llm(m["id"])
            await test_llm.ainvoke("ping")
            online = True
        except Exception:
            online = False
        models_status.append({**m, "online": online})
    return success_response(models_status)


@app.post("/api/langchain/chat")
async def api_chat(req: ChatRequest):
    """通用 AI 聊天对话"""
    try:
        model_id = req.model or DEEPSEEK_MODEL
        llm = get_chat_llm(model_id)
        from langchain_core.messages import HumanMessage, AIMessage, SystemMessage

        messages = []
        for msg in req.messages:
            role = msg.get("role", "user")
            content = msg.get("content", "")
            if role == "system":
                messages.append(SystemMessage(content=content))
            elif role == "assistant":
                messages.append(AIMessage(content=content))
            else:
                messages.append(HumanMessage(content=content))

        result = await llm.ainvoke(messages)
        return make_response({"content": result.content, "model": model_id})
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# ===== 启动 =====
if __name__ == "__main__":
    import uvicorn
    from config import HOST, PORT
    uvicorn.run(app, host=HOST, port=PORT)
