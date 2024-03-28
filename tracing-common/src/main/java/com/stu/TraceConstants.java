package com.stu;

public interface TraceConstants {

    // 需要设置成 "TRACE_ID" logback 配置才能从 [%X{TRACE_ID}] 参数中获取到 trace id
    String TRACE_ID = "TRACE_ID";
    String LOG_BACK_FILE = "logback-spring.xml";
}
