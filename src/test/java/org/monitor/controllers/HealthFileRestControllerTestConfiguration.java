package org.monitor.controllers;

import org.monitor.configs.entities.AppConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class HealthFileRestControllerTestConfiguration {
    @Bean
    public AppConfig appConfig() {
        AppConfig config = new AppConfig();

        // Set your test values directly
        AppConfig.CheckConfig check = new AppConfig.CheckConfig();
        check.setFileDateFormatter("ddMMyyyyHHmm");
        check.setOutputDir("/tmp");
        check.setIntervalMs(1000L);
        config.setCheck(check);

        return config;
    }
}
