package com.stu;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ServiceA {
    public void serviceStr() {
        String tracId = MDC.get(TracingConstants.TRANCE_ID);
        log.info("trace id get from mdc: {}", tracId);
    }
}
