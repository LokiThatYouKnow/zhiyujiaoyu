package com.mengzhidui.a7springboot.utils;


import java.util.UUID;

public class IdGenerator {
    public static Long generateUniqueId() {
        UUID uuid = UUID.randomUUID();
        return Math.abs(uuid.getMostSignificantBits()); // 取绝对值避免负数
    }
}
