package com.stu.interceptor;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.stu.TraceConstants;
import com.stu.TraceUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TraceInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("TraceInterceptor#preHandle");
        String traceId = request.getHeader(TraceConstants.TRACE_ID); // 继承前序请求的 traceId
        if (!StringUtils.hasText(traceId)) {
            traceId = TraceUtils.generateTraceId();
        }
        MDC.put(TraceConstants.TRACE_ID, traceId);
        // 同时在 ttlMDC 中保存一份
        ttlMDC.get().put(TraceConstants.TRACE_ID, traceId);

        log.info("request url: {}", request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.remove(TraceConstants.TRACE_ID);
        ttlMDC.get().remove(TraceConstants.TRACE_ID);
    }

    public static TransmittableThreadLocal<Map<String, String>> ttlMDC = new TransmittableThreadLocal<>() {

        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<>();
        }

        /**
         * before submit/execute
         * 在线程开始执行之前
         * 父子线程数据传输之前，将由父线程继承来的 traceId  copy 一份给 MDC
         */
        @Override
        protected void beforeExecute() {
            Map<String, String> mdc = get();
            mdc.forEach(MDC::put);
        }

        /**
         * after submit/execute
         * 线程执行完后，执行 clear 操作
         */
        @Override
        protected void afterExecute() {
            MDC.clear();
        }
    };
}
