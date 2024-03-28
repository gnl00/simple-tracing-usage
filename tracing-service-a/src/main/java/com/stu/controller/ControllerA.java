package com.stu.controller;

import com.stu.service.ServiceA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/api/a")
public class ControllerA {

    private final RestTemplate restTemplate;

    public ControllerA(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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

    @GetMapping("/cross")
    public String crossSrv() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8002/api/b/reqB", String.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        return "ControllerA#pool();request for service b failed";
    }
}
