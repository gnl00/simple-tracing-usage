package com.stu.aspect;

import com.stu.TraceConstants;
import com.stu.TraceUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy
public class TraceAspect {

    @Pointcut("execution(* com.stu..controller.*.*(..))")
    public void controllerPointCut() {}

    @Pointcut("execution(* com.stu..*.*(..)) && !execution(* com.stu..aspect.*(..))")
    public void tracePointCut() {}

    /**
     * 本来是想使用 Aspect 来添加 traceId，
     * 但是请求进来后先经过 Servlet 层，在切面添加 traceId 的话就稍微有点晚了
     */
    // @Before(value = "controllerPointCut()")
    public void beforeController(JoinPoint jp) {
        String className = jp.getTarget().getClass().getName();
        String methodName = jp.getSignature().getName();
        StringBuilder argsBuilder = new StringBuilder();

        for (Object arg : jp.getArgs()) {
            argsBuilder.append(arg).append(",");
        }
        if (!argsBuilder.isEmpty()) {
            argsBuilder.deleteCharAt(argsBuilder.length() - 1);
        }

        String traceId = TraceUtils.generateTraceId();
        MDC.put(TraceConstants.TRACE_ID, traceId);
        log.info("adding trace-id: {} for: {}#{}({})", traceId, className, methodName, argsBuilder.toString());
    }

    // @Before(value = "tracePointCut()")
    public void beforeTrace(JoinPoint jp) {
        log.info("setting thread-local trace id for method: {}", jp.getSignature().getName());
    }

    // @After(value = "tracePointCut()")
    public void afterTrace(JoinPoint jp) {
        // ...
    }
}
