package com.mengzhidui.a7springboot.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.mengzhidui.a7springboot.entity.po.AdminPO;
import com.mengzhidui.a7springboot.interfaces.AppUser;
import com.mengzhidui.a7springboot.response.R;
import com.mengzhidui.a7springboot.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取图形验证码
     * GET /api/auth/captcha
     */
    @GetMapping("/captcha")
    public R<Map<String, String>> getCaptcha() {
        // 使用 Hutool 生成线段干扰验证码：200x80, 4位字符, 50条干扰线
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(200, 80, 4, 50);
        String code = captcha.getCode();
        String key = UUID.randomUUID().toString();

        // 存入 Redis，5 分钟过期
        redisTemplate.opsForValue().set("captcha:" + key, code, 5, TimeUnit.MINUTES);

        Map<String, String> data = new HashMap<>();
        data.put("captchaKey", key);
        data.put("captchaImage", "data:image/png;base64," + captcha.getImageBase64());

        log.debug("生成验证码: key={}, code={}", key, code);
        return R.success("success", data);
    }

    /**
     * 统一登录接口（自动识别学生/教师/管理员角色）
     * POST /api/auth/login
     * Body: { username, password, captchaKey, captchaCode }
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String captchaKey = body.get("captchaKey");
        String captchaCode = body.get("captchaCode");

        // 参数校验
        if (username == null || username.isBlank()) {
            return R.error("请输入账号");
        }
        if (password == null || password.isBlank()) {
            return R.error("请输入密码");
        }
        if (captchaKey == null || captchaCode == null || captchaCode.isBlank()) {
            return R.error("请输入验证码");
        }

        // 验证图形验证码
        String redisKey = "captcha:" + captchaKey;
        Object storedCodeObj = redisTemplate.opsForValue().get(redisKey);
        if (storedCodeObj == null) {
            return R.error("验证码已过期，请刷新后重试");
        }
        String storedCode = storedCodeObj.toString();
        if (!storedCode.equalsIgnoreCase(captchaCode)) {
            return R.error("验证码错误");
        }
        // 验证通过，删除验证码（一次性使用）
        redisTemplate.delete(redisKey);

        // Spring Security 认证
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 获取用户角色
            AppUser user = (AppUser) auth.getPrincipal();
            String role = user.getType().toUpperCase(); // STUDENT / TEACHER / ADMIN
            String roleLower = role.toLowerCase();

            // 生成 JWT（含角色信息）
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", role);

            // 如果是管理员，额外加入 isSuperAdmin 字段
            if (user instanceof AdminPO admin) {
                claims.put("isSuperAdmin", admin.getIsSuperAdmin());
            }
            String token = jwtUtils.generateToken(username, claims);

            // 存入 Redis（与现有模式一致：jwt:{role}:token:{token}）
            String tokenRedisKey = "jwt:" + roleLower + ":token:" + token;
            redisTemplate.opsForValue().set(tokenRedisKey, username,
                    jwtUtils.extractExpiration(token).getTime() - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS);

            log.info("统一登录成功: username={}, role={}", username, role);
            return R.success("登录成功", token);

        } catch (BadCredentialsException e) {
            return R.error("账号或密码错误");
        } catch (AuthenticationException e) {
            log.warn("登录失败: username={}, reason={}", username, e.getMessage());
            return R.error("登录失败：" + e.getMessage());
        }
    }
}
