package com.stu;

import java.util.UUID;

public class TracingUtils {
    public static String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}
