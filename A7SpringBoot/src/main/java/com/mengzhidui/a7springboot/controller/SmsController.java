package com.mengzhidui.a7springboot.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class SmsController {

    private final Map<String, String> codeMap = new ConcurrentHashMap<>();
    private final Map<String, Long> timeMap = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @PostMapping("/sms/send")
    public Map<String, Object> sendCode(@RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        String phone = body.get("phone");

        if (phone == null || !phone.matches("^1\\d{10}$")) {
            result.put("code", 400);
            result.put("msg", "手机号格式不正确");
            result.put("data", null);
            return result;
        }

        Long lastSend = timeMap.get(phone);
        if (lastSend != null && System.currentTimeMillis() - lastSend < 60_000) {
            long remain = 60 - (System.currentTimeMillis() - lastSend) / 1000;
            result.put("code", 400);
            result.put("msg", "请 " + remain + " 秒后再试");
            result.put("data", null);
            return result;
        }

        String code = String.format("%06d", random.nextInt(1_000_000));
        codeMap.put(phone, code);
        timeMap.put(phone, System.currentTimeMillis());

        System.out.println("===== SMS =====");
        System.out.println("Phone: " + phone + "  Code: " + code);
        System.out.println("==============");

        result.put("code", 200);
        result.put("msg", "验证码已发送");
        result.put("data", null);
        return result;
    }

    @PostMapping("/sms/verify")
    public Map<String, Object> verifyCode(@RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        String phone = body.get("phone");
        String code = body.get("code");

        if (phone == null || code == null) {
            result.put("code", 400);
            result.put("msg", "手机号或验证码不能为空");
            result.put("data", null);
            return result;
        }

        String storedCode = codeMap.get(phone);
        Long sendTime = timeMap.get(phone);

        if (storedCode == null) {
            result.put("code", 400);
            result.put("msg", "请先获取验证码");
            result.put("data", null);
            return result;
        }

        if (sendTime != null && System.currentTimeMillis() - sendTime > 300_000) {
            codeMap.remove(phone);
            timeMap.remove(phone);
            result.put("code", 400);
            result.put("msg", "验证码已过期，请重新获取");
            result.put("data", null);
            return result;
        }

        if (!storedCode.equals(code)) {
            result.put("code", 400);
            result.put("msg", "验证码错误");
            result.put("data", null);
            return result;
        }

        codeMap.remove(phone);
        timeMap.remove(phone);
        result.put("code", 200);
        result.put("msg", "验证成功");
        result.put("data", null);
        return result;
    }
}
