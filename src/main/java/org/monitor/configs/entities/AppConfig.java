package org.monitor.configs.entities;

import org.monitor.entities.ServiceInfo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Data to hold application yml specified properties.
 */
@Component
@ConfigurationProperties
@Data
public class AppConfig {
    private CheckConfig check;
    private List<ServiceInfo> services;

    /**
     * Default values loaded under the scenario where it is not stated in yml.
     */
    @Data
    public static class CheckConfig {
        private long intervalMs = 30000;
        private String outputDir = "/app/output";
        private String fileDateFormatter = "ddMMyyyyHHmm";
    }
}
