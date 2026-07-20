"""
A7 教学实训智能体 - Pydantic 请求/响应模型
"""

from pydantic import BaseModel, Field
from typing import Optional, List, Dict, Any


# ===== 通用 =====
class HealthResponse(BaseModel):
    status: str
    llm_model: str
    embedding_model: str
    deepseek_connected: bool
    chroma_ok: bool


class ErrorResponse(BaseModel):
    code: int = 400
    msg: str
    data: Optional[Any] = None


class SuccessResponse(BaseModel):
    code: int = 200
    msg: str = "success"
    data: Optional[Any] = None


# ===== 教师端请求模型 =====
class KnowledgeOrganizationRequest(BaseModel):
    data: str = Field(..., description="课程名称/知识点")
    teacher_username: str = Field(..., description="教师用户名")
    resource_name: str = Field(default="", description="课件名称")
    start_time: str = Field(default="", description="创建时间")
    input: str = Field(default="", description="详细需求")


class PractiseDesignRequest(BaseModel):
    data: str = Field(..., description="课程名称/知识点")
    teacher_username: str = Field(..., description="教师用户名")
    resource_name: str = Field(default="", description="课件名称")
    start_time: str = Field(default="", description="创建时间")
    input: str = Field(default="", description="实训需求")


class DesignIssueRequest(BaseModel):
    data: str = Field(..., description="课程名称")
    teacher_username: str = Field(..., description="教师用户名")
    issue_name: str = Field(..., description="练习名称")
    input: str = Field(default="", description="出题要求")
    start_time: str = Field(default="", description="开始时间")
    end_time: str = Field(default="", description="结束时间")


class ArrangeTimeRequest(BaseModel):
    data: str = Field(..., description="课程名称")
    teacher_username: str = Field(..., description="教师用户名")


class GetIssueAnswerDetailRequest(BaseModel):
    question_id: str = Field(..., description="问题ID")
    data: str = Field(..., description="课程名称")
    teacher_username: str = Field(..., description="教师用户名")


class IssueListRequest(BaseModel):
    data: str = Field(..., description="课程名称")
    teacher_username: str = Field(..., description="教师用户名")


class ResourceListRequest(BaseModel):
    data: str = Field(..., description="课程名称")
    teacher_username: str = Field(..., description="教师用户名")


class DeleteIssueRequest(BaseModel):
    question_id: str = Field(..., description="问题ID")


class DeleteResourceRequest(BaseModel):
    resource_id: str = Field(..., description="资源ID")


class DeleteCourseRequest(BaseModel):
    document_id: str = Field(default="", description="文档ID")
    teacher_username: str = Field(..., description="教师用户名")
    data: str = Field(..., description="课程名称")


class SearchCoursewareRequest(BaseModel):
    course_name: str = Field(..., description="课程名称")
    teacher_username: str = Field(..., description="教师用户名")
    search_prompt: str = Field(default="", description="搜索提示词")


class AddCourseKnowledgeRequest(BaseModel):
    course_name: str = Field(..., description="课程名称")
    teacher_username: str = Field(..., description="教师用户名")
    document_id: Optional[str] = Field(default=None, description="文档ID（如有）")
    file_content: Optional[str] = Field(default=None, description="Base64编码文件内容")
    file_name: Optional[str] = Field(default=None, description="文件名")


# ===== 学生端请求模型 =====
class AnswerRequest(BaseModel):
    data: str = Field(..., description="课程名称/知识点")
    input: str = Field(..., description="学生问题")


class SelfPractiseRequest(BaseModel):
    student_username: str = Field(..., description="学生用户名")
    input: str = Field(..., description="练习需求")
    data: str = Field(..., description="课程名称")
    teacher_username: str = Field(..., description="教师用户名")


class SelfPractiseCorrectRequest(BaseModel):
    student_username: str = Field(..., description="学生用户名")
    input: str = Field(..., description="学生答案(JSON)")
    data: str = Field(..., description="课程名称")
    teacher_username: str = Field(..., description="教师用户名")
    question_id: str = Field(default="", description="问题ID")


class CoursePractiseRequest(BaseModel):
    data: str = Field(..., description="课程名称")
    teacher_username: str = Field(..., description="教师用户名")


class CoursePractiseCorrectAnswerRequest(BaseModel):
    input: str = Field(..., description="学生答案(JSON)")
    student_username: str = Field(..., description="学生用户名")
    data: str = Field(..., description="课程名称")
    teacher_username: str = Field(..., description="教师用户名")
    question_id: str = Field(..., description="问题ID")


# ===== 通用聊天请求模型 =====
class ChatRequest(BaseModel):
    model: str = Field(default="deepseek-v4-pro", description="模型 ID")
    messages: List[Dict[str, str]] = Field(..., description="消息列表，每项含 role 和 content")
