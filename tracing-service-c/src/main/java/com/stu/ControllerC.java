package com.stu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/c")
public class ControllerC {
    @GetMapping("/reqC")
    public String reqC() {
        log.info("into controller-c");
        return "[TRACING-SERVICE-C]ControllerC#reqC";
    }
}
