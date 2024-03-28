package com.stu.service;

import com.stu.TraceConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Slf4j
@Service
public class ServiceA {

    /**
     * 只需要使用自定义的 asyncExecutor 来包装需要执行的任务
     * 就能让自线程获取到父线程的 traceId
     */
    private final Executor asyncExecutor;

    public ServiceA(Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    public void srvOnce() {
        String traceId = MDC.get(TraceConstants.TRACE_ID);
        log.info("trace id in serviceA: {}", traceId);

        Thread threadA = new Thread(() -> {
            String traceIdInner = MDC.get(TraceConstants.TRACE_ID);
            log.info("trace id inner in serviceA#innerThread: {}", traceIdInner);
        });
        // threadA.start();
        asyncExecutor.execute(threadA);
    }

    public void srvPool() {
        log.info("trace id outer: {}", MDC.get(TraceConstants.TRACE_ID));

        int threadCount = 10;
        for (int i = 0; i < threadCount; i++) {
            final int finalI = i;
            Thread thread = new Thread(() -> {
                log.info("thread-{}, trace-id: {}", finalI, MDC.get(TraceConstants.TRACE_ID));
            });
            // ttlExecutor.execute(thread);
            asyncExecutor.execute(thread);
        }
    }
}
