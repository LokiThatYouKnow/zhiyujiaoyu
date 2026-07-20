"""
A7 教学实训智能体 - 学生端 LangChain 链
"""

import uuid
from .base import run_chain
from models.prompts import (
    STUDENT_ANSWER_SYSTEM, STUDENT_ANSWER_HUMAN,
    SELF_PRACTISE_SYSTEM, SELF_PRACTISE_HUMAN,
    SELF_PRACTISE_CORRECT_SYSTEM, SELF_PRACTISE_CORRECT_HUMAN,
    COURSE_PRACTISE_SYSTEM, COURSE_PRACTISE_HUMAN,
    COMPARE_ANSWER_SYSTEM, COMPARE_ANSWER_HUMAN,
    build_prompt,
)
from rag.retriever import retrieve_context


async def student_answer(
    course_name: str,
    question: str,
) -> dict:
    """学生AI问答：基于课程内容解答问题"""
    context = retrieve_context(course_name, question)

    prompt = build_prompt(STUDENT_ANSWER_SYSTEM, STUDENT_ANSWER_HUMAN)
    result = await run_chain(
        prompt,
        course_name=course_name,
        context=context,
        question=question,
    )

    return {"answerQuestion": result}


async def self_practise(
    course_name: str,
    student_username: str,
    teacher_username: str,
    user_request: str = "",
) -> dict:
    """自主练习：根据需求生成练习题"""
    context = retrieve_context(course_name, course_name)

    prompt = build_prompt(SELF_PRACTISE_SYSTEM, SELF_PRACTISE_HUMAN)
    result = await run_chain(
        prompt,
        course_name=course_name,
        context=context,
        user_request=user_request or "请根据课程内容生成综合练习题",
        student_username=student_username,
    )

    question_id = f"sp_{uuid.uuid4().hex[:12]}"

    return {
        "studentsReviewExercises": result,
        "studentsReviewExercisesQuestionId": question_id,
    }


async def self_practise_correct(
    course_name: str,
    student_username: str,
    teacher_username: str,
    student_answer: str,
    question_id: str = "",
) -> dict:
    """练习纠错：批改学生答案"""
    context = retrieve_context(course_name, course_name)

    prompt = build_prompt(SELF_PRACTISE_CORRECT_SYSTEM, SELF_PRACTISE_CORRECT_HUMAN)
    result = await run_chain(
        prompt,
        course_name=course_name,
        context=context,
        student_username=student_username,
        student_answer=student_answer,
    )

    return {"studentCorrectErrors": result}


async def course_practise(
    course_name: str,
    teacher_username: str,
) -> dict:
    """课程练习：获取最新题目"""
    context = retrieve_context(course_name, course_name)

    prompt = build_prompt(COURSE_PRACTISE_SYSTEM, COURSE_PRACTISE_HUMAN)
    result = await run_chain(
        prompt,
        course_name=course_name,
        context=context,
        teacher_username=teacher_username,
    )

    return {"studentGetRecentQuestion": result}


async def course_practise_correct_answer(
    course_name: str,
    student_username: str,
    teacher_username: str,
    student_answer: str,
    question_id: str,
) -> dict:
    """答案对比：对比学生答案与标准答案"""
    context = retrieve_context(course_name, course_name)

    prompt = build_prompt(COMPARE_ANSWER_SYSTEM, COMPARE_ANSWER_HUMAN)
    result = await run_chain(
        prompt,
        course_name=course_name,
        context=context,
        student_username=student_username,
        student_answer=student_answer,
        question_id=question_id,
    )

    return {"compareAnswer": result}
