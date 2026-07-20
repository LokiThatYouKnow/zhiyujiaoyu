"""
A7 教学实训智能体 - 配置模块
LLM: DeepSeek V4 Pro (Cloud API)
Embedding: Ollama (本地轻量，无需 GPU)
Vector Store: ChromaDB
"""

import os

# ===== 服务配置 =====
HOST = "0.0.0.0"
PORT = 9084

# ===== DeepSeek Cloud API 配置 =====
DEEPSEEK_API_KEY = os.getenv("DEEPSEEK_API_KEY", "sk-your-api-key-here")
DEEPSEEK_BASE_URL = os.getenv("DEEPSEEK_BASE_URL", "https://api.deepseek.com")
DEEPSEEK_MODEL = os.getenv("DEEPSEEK_MODEL", "deepseek-v4-pro")

# LLM 参数
LLM_TEMPERATURE = 0.1
LLM_TOP_P = 0.9
LLM_MAX_TOKENS = 4096
LLM_TIMEOUT = 300  # 5分钟超时

# ===== Ollama 配置（仅用于 Embedding）=====
OLLAMA_BASE_URL = os.getenv("OLLAMA_BASE_URL", "http://localhost:11434")
EMBEDDING_MODEL = os.getenv("EMBEDDING_MODEL", "nomic-embed-text")

# ===== ChromaDB 配置 =====
CHROMA_PERSIST_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "chroma_db")

# ===== 知识库配置 =====
KNOWLEDGE_BASE_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "knowledge_base")
CHUNK_SIZE = 1000
CHUNK_OVERLAP = 200
MAX_RETRIEVED_DOCS = 6

# ===== 文件上传配置 =====
ALLOWED_EXTENSIONS = {".pdf", ".docx", ".txt", ".md"}
MAX_FILE_SIZE = 50 * 1024 * 1024  # 50MB

# ===== Spring Boot 后端配置 =====
SPRING_BOOT_URL = os.getenv("SPRING_BOOT_URL", "http://localhost:9080")

# ===== 可用模型列表 =====
AVAILABLE_MODELS = [
    {
        "id": "deepseek-v4-pro",
        "name": "DeepSeek V4 Pro",
        "description": "最新旗舰模型，综合能力最强",
        "provider": "deepseek",
    },
    {
        "id": "deepseek-chat",
        "name": "DeepSeek Chat",
        "description": "通用对话模型，速度快",
        "provider": "deepseek",
    },
]
