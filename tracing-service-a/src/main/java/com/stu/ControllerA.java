package com.stu;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/a")
public class ControllerA {

    @Autowired
    private ServiceA serviceA;

    @GetMapping("/str")
    public String str() {
        String traceId = TracingUtils.generateTraceId();
        MDC.put(TracingConstants.TRANCE_ID, traceId);
        log.trace("tracing id: {}, for method str() in Class: {}", traceId, getClass());
        serviceA.serviceStr();
        return "ControllerA#str()";
    }
}
