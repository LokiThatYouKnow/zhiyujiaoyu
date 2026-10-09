import axios from '../utils/axios'

export interface ChatMessage {
  role: 'system' | 'user' | 'assistant'
  content: string
}

export interface ChatRequest {
  model: string
  messages: ChatMessage[]
}

export interface ChatResponse {
  content: string
  model: string
}

export interface ModelInfo {
  id: string
  name: string
  description: string
  provider: string
  online: boolean
}

/** 发送聊天消息 */
export async function sendChatApi(role: 'teacher' | 'student', data: ChatRequest): Promise<ChatResponse> {
  const res = await axios.post(`/${role}/chat`, data, { timeout: 180000 })
  return res.data?.data
}

/** 获取可用模型列表 */
export async function getModelsApi(role: 'teacher' | 'student'): Promise<ModelInfo[]> {
  const res = await axios.get(`/${role}/models`)
  return res.data?.data || []
}
