package com.mengzhidui.a7springboot.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheManagerUtils {

    private final CacheManager cacheManager;

    //例子,目前业务的清除缓存尚未开发
    public void evictUserListAndBestStudentCache() {
        // 删除 userList 缓存
        Cache userListCache = cacheManager.getCache("userList");
        if (userListCache != null) {
            userListCache.evict("all");
        }

        // 删除 bestStudent 缓存
        Cache bestStudentCache = cacheManager.getCache("bestStudent");
        if (bestStudentCache != null) {
            bestStudentCache.evict("all");
        }
    }
}
