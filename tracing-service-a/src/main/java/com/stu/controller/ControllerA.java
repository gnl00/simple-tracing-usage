package com.stu.controller;

import com.stu.service.ServiceA;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/once")
    public String once() {
        serviceA.srvOnce();
        return "ControllerA#once()";
    }

    @GetMapping("/pool")
    public String pool() {
        serviceA.srvPool();
        return "ControllerA#pool()";
    }
}
