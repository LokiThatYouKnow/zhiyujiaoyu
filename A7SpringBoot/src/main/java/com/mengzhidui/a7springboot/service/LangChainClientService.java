package com.mengzhidui.a7springboot.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * LangChain AI 服务调用客户端
 * 替代原有的 COZE API，将 AI 请求转发到 Python LangChain 服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LangChainClientService {

    @Value("${langchain.service.url}")
    private String langchainUrl;

    @Value("${langchain.service.timeout}")
    private long timeout;

    private final Gson gson = new Gson();

    /**
     * 通用 POST 请求方法
     * @param path 端点路径（如 /teacher/knowledgeOrganization）
     * @param params 请求参数 Map
     * @return 响应 JsonObject
     */
    public JsonObject post(String path, Map<String, String> params) {
        try {
            String fullUrl = langchainUrl + path;
            String jsonBody = gson.toJson(params);

            log.info("LangChain API 请求: {} {}", path, params.keySet());

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    .readTimeout(timeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                    .build();

            RequestBody body = RequestBody.create(
                    jsonBody,
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(fullUrl)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    log.debug("LangChain 响应: {}", responseBody);

                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                    int code = jsonObject.get("code").getAsInt();

                    if (code == 200) {
                        // 解析 data 数组的第一个元素
                        if (jsonObject.has("data") && !jsonObject.get("data").isJsonNull()) {
                            var dataArray = jsonObject.getAsJsonArray("data");
                            if (dataArray != null && dataArray.size() > 0) {
                                return dataArray.get(0).getAsJsonObject();
                            }
                        }
                        return new JsonObject();
                    } else {
                        String msg = jsonObject.has("msg") ?
                                jsonObject.get("msg").getAsString() : "AI服务调用失败";
                        log.error("LangChain 服务返回错误: {}", msg);
                        JsonObject errorResult = new JsonObject();
                        errorResult.addProperty("error", msg);
                        return errorResult;
                    }
                } else {
                    log.error("LangChain 服务HTTP错误: {}", response.code());
                    JsonObject errorResult = new JsonObject();
                    errorResult.addProperty("error", "HTTP " + response.code());
                    return errorResult;
                }
            }
        } catch (Exception e) {
            log.error("LangChain 服务调用异常", e);
            JsonObject errorResult = new JsonObject();
            errorResult.addProperty("error", e.getMessage());
            return errorResult;
        }
    }

    // ===== 教师端方法 =====

    public JsonObject knowledgeOrganization(Map<String, String> params) {
        return post("/teacher/knowledgeOrganization", params);
    }

    public JsonObject practiseDesign(Map<String, String> params) {
        return post("/teacher/practiseDesign", params);
    }

    public JsonObject designIssue(Map<String, String> params) {
        return post("/teacher/designIssue", params);
    }

    public JsonObject arrangeTime(Map<String, String> params) {
        return post("/teacher/arrangeTime", params);
    }

    public JsonObject getIssueAnswerDetail(Map<String, String> params) {
        return post("/teacher/getIssueAnswerDetail", params);
    }

    public JsonObject addCourseKnowledge(Map<String, String> params) {
        return post("/teacher/addCourseKnowledge", params);
    }

    public JsonObject deleteCourse(Map<String, String> params) {
        return post("/teacher/deleteCourse", params);
    }

    public JsonObject deleteIssue(Map<String, String> params) {
        return post("/teacher/deleteIssue", params);
    }

    public JsonObject deleteResource(Map<String, String> params) {
        return post("/teacher/deleteResource", params);
    }

    public JsonObject getIssueList(Map<String, String> params) {
        return post("/teacher/getIssueList", params);
    }

    public JsonObject getResourceList(Map<String, String> params) {
        return post("/teacher/getResourceList", params);
    }

    public JsonObject searchCourseware(Map<String, String> params) {
        return post("/teacher/searchCourseware", params);
    }

    /** 通用聊天对话 — 接受任意 JSON 对象 */
    public JsonObject chatRaw(String jsonBody) {
        try {
            String fullUrl = langchainUrl + "/chat";
            log.info("LangChain Chat API 请求");

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    .readTimeout(timeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                    .build();

            RequestBody body = RequestBody.create(
                    jsonBody,
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(fullUrl)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
                    int code = jsonObject.get("code").getAsInt();
                    if (code == 200) {
                        var dataArray = jsonObject.getAsJsonArray("data");
                        if (dataArray != null && dataArray.size() > 0) {
                            return dataArray.get(0).getAsJsonObject();
                        }
                        // data 数组为空
                        JsonObject err = new JsonObject();
                        err.addProperty("error", "AI 返回了空结果，请重试");
                        return err;
                    }
                    // Python 返回了业务错误（code != 200）
                    String msg = jsonObject.has("msg") ? jsonObject.get("msg").getAsString() : "未知错误";
                    JsonObject err = new JsonObject();
                    err.addProperty("error", msg);
                    return err;
                }
                // HTTP 非 2xx：读取 Python 错误详情
                log.error("Chat API HTTP {}: {}", response.code(), responseBody);
                JsonObject err = new JsonObject();
                err.addProperty("error", "AI 服务异常 (HTTP " + response.code() + ")，请检查 LangChain 服务日志");
                return err;
            }
        } catch (java.net.ConnectException e) {
            log.error("Chat API 连接失败 — LangChain 服务可能未启动", e);
            JsonObject err = new JsonObject();
            err.addProperty("error", "AI 服务未连接，请确保 LangChain 服务已启动（端口 8000）");
            return err;
        } catch (Exception e) {
            log.error("Chat API 调用异常", e);
            JsonObject err = new JsonObject();
            err.addProperty("error", "Chat API 调用失败: " + e.getMessage());
            return err;
        }
    }

    /** 获取可用模型列表 */
    public JsonObject listModels() {
        // GET 请求
        try {
            String fullUrl = langchainUrl + "/models";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(fullUrl)
                    .get()
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return gson.fromJson(responseBody, JsonObject.class);
                }
            }
        } catch (Exception e) {
            log.warn("获取模型列表失败 (LangChain 服务未启动或不可用): {}", e.getMessage());
        }
        return new JsonObject();
    }

    // ===== 学生端方法 =====

    public JsonObject studentAnswer(Map<String, String> params) {
        return post("/student/answer", params);
    }

    public JsonObject selfPractise(Map<String, String> params) {
        return post("/student/selfPractise", params);
    }

    public JsonObject selfPractiseCorrect(Map<String, String> params) {
        return post("/student/selfPractiseCorrect", params);
    }

    public JsonObject coursePractise(Map<String, String> params) {
        return post("/student/coursePractise", params);
    }

    public JsonObject coursePractiseCorrectAnswer(Map<String, String> params) {
        return post("/student/coursePractiseCorrectAnswer", params);
    }
}
