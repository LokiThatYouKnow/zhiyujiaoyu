package com.mengzhidui.a7springboot.listen;

import com.mengzhidui.a7springboot.utils.CacheManagerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisKeyExpirationListener implements MessageListener {

    private final RedisTemplate<String, Object> redisTemplate;

    private final CacheManagerUtils cacheManagerUtils;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        // 处理 JWT 过期事件
        if (expiredKey.startsWith("jwt:admin:token:")) {
            // 提取 token 和 username
            String token = expiredKey.substring("jwt:token:".length());
            String username = (String) redisTemplate.opsForValue().get(expiredKey);

            // 清理管理员相关缓存
            if (username != null) {
                //清除相关数据(未写)

                log.info("Token expired: {}, cleaned admin cache for: {}", token, username);
            }
        }

        if (expiredKey.startsWith("jwt:student:token:")) {
            // 提取 token 和 username
            String token = expiredKey.substring("jwt:student:token:".length());
            String username = (String) redisTemplate.opsForValue().get(expiredKey);

            // 清理学生相关缓存
            if (username != null) {
                //清除相关数据(未写)

                log.info("Token expired: {}, cleaned student cache for: {}", token, username);
            }
        }

        if (expiredKey.startsWith("jwt:teacher:token:")) {
            // 提取 token 和 username
            String token = expiredKey.substring("jwt:token:".length());
            String username = (String) redisTemplate.opsForValue().get(expiredKey);

            // 清理教师相关缓存
            if (username != null) {
                //清除相关数据(未写)

                log.info("Token expired: {}, cleaned teacher cache for: {}", token, username);
            }
        }
    }
}