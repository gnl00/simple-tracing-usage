package com.stu.listener;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.stu.TraceConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${project.build-dir}")
    private String buildDir;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("## application started");
        log.info("build dir: {}", buildDir);
        Path destPath = Paths.get(buildDir, TraceConstants.LOG_BACK_FILE);
        try (
            InputStream is = getClass().getClassLoader().getResourceAsStream(TraceConstants.LOG_BACK_FILE);
            FileOutputStream fos = new FileOutputStream(destPath.toString());
        ) {
            Assert.notNull(is, "no logback configuration found!");
            is.transferTo(fos);
            log.info("logback configuration copied!");
        } catch (IOException e) {
            log.error("logback configuration copy failed， occurred IOException: {}", e.getMessage());
        }
    }
}
