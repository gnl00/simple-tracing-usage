package com.stu.listener;

import com.stu.TracingConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.jar.Manifest;

@Slf4j
@Component
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${spring.application.name}")
    private String appName;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("## application started");
        try {
            ClassPathResource logResource = new ClassPathResource(TracingConstants.LOG_BACK_FILE);
            File logFile = logResource.getFile();

            log.info("log file: {} exist? {}", logFile.getName(), logFile.exists());
            log.info("log file path: {}", logFile.getAbsolutePath());

            if (logFile.exists()) {
                // 这样一来就有一个强制要求：pom.xml 中的 artifactId 必须和项目文件夹名称一致
                log.info("current artifact: {}", appName);
                File dest = new File(String.valueOf(Paths.get(appName,"/target/classes/logback-spring.xml")));
                Files.copy(Paths.get(logFile.getPath()), Paths.get(dest.toURI()), StandardCopyOption.REPLACE_EXISTING);
            } else {
                log.error("no logback configuration found!");
            }
        } catch (IOException e) {
            log.error("logback copy to target failed， occurred IOException: {}", e.getMessage());
        }
    }
}
