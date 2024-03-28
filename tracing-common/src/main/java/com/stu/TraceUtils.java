package com.stu;

import java.util.UUID;

public class TraceUtils {
    public static String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}
