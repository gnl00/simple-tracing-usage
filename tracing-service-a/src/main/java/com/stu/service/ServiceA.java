package com.stu.service;

import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.stu.TraceConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class ServiceA {

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
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        Executor ttlExecutor = TtlExecutors.getTtlExecutor(executorService);

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
