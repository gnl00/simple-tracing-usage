package com.stu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/api/b")
public class ControllerB {
    private final RestTemplate restTemplate;
    public ControllerB(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/reqB")
    public String reqB() {
        log.info("into controller-b");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8003/api/c/reqC", String.class);
        return responseEntity.getBody();
    }
}
