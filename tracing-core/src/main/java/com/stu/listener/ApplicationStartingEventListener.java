package com.stu.listener;

import com.stu.TracingConstants;
import com.stu.TracingUtils;
import org.slf4j.MDC;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

/**
 * ApplicationStartingEventListener 需要添加到 spring.factories 才能生效
 */
public class ApplicationStartingEventListener implements ApplicationListener<ApplicationStartingEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        // System.out.println("## application starting"); // 此时 Logback 还未初始化完成

        /**
         * 设置一个初始的 trace id 防止出现类似下面这样子的日志，trace id 为空
         * 2024-03-27 17:12:56.866 [main] INFO  -[]-  c.stu.ServiceAMain#[logStarting,50]
         * 2024-03-27 17:12:56.868 [main] INFO  -[]-  c.stu.ServiceAMain#[logStartupProfileInfo,654]
         * ...
         * 或许也不用添加？毕竟项目刚启动的时候不需要追踪任何请求？
         * 也说不准，万一项目启动的时候发送了一些初始化请求呢？
         * ...
         * 还有一个办法是从 Logback 方面判断，如果 trace id 为空就不输出 -[]-
         * 可以使用自定义的转换器 TraceIdPatternConverter 来处理，可搜索关键字 "logback Converter"
         * <pre>{@code
         * public class TraceIdPatternConverter extends ClassicConverter {
         *     @Override
         *     public String convert(ILoggingEvent event) {
         *         String traceId = event.getMDCPropertyMap().get("TRACE_ID");
         *         return traceId != null ? "[" + traceId + "]" : "-[]-";
         *     }
         * }
         * }</pre>
         */
        MDC.put(TracingConstants.TRANCE_ID, TracingUtils.generateTraceId());
    }
}
