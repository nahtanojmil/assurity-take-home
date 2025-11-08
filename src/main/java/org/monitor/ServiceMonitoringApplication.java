package org.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class ServiceMonitoringApplication {
    public static void main(String[] args) {
        log.info("Starting Monitoring Service Application");
        SpringApplication.run(ServiceMonitoringApplication.class, args);
    }
}