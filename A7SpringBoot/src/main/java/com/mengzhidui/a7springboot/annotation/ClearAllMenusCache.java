package com.mengzhidui.a7springboot.annotation;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Caching(
        evict = {
                @CacheEvict(value = "menus", key = "'admin_menu'"),
                @CacheEvict(value = "menus", key = "'student_menu'"),
                @CacheEvict(value = "menus", key = "'teacher_menu'")
        }
)
public @interface ClearAllMenusCache {
}
