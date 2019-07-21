package com.seckill.common.util;


import java.util.UUID;

public class UUIDutil {
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
