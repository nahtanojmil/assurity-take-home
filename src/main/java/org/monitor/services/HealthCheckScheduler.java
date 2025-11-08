package org.monitor.services;

import org.monitor.configs.EnvConfigLoader;
import org.monitor.configs.entities.AppConfig;
import org.monitor.controllers.HealthCheckController;
import org.monitor.entities.HealthStatus;
import org.monitor.entities.ServiceInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *      Service scheduler that periodically calls Controller checks to
 *      write into json object.
 * </p>
 * <p>
 *     interval is defined within application.yml.
 * </p>
 *
 */
@Service
@Slf4j
public class HealthCheckScheduler {

    private final AppConfig appConfig;
    private final HealthCheckController checker;
    private final ObjectMapper mapper;
    private final EnvConfigLoader envConfigLoader;

    private final List<HealthStatus> latestStatus = new ArrayList<>();

    private List<ServiceInfo> serviceInfoList;

    public HealthCheckScheduler(AppConfig appConfig,
                                HealthCheckController checker,
                                ObjectMapper mapper,
                                EnvConfigLoader loader) {
        this.appConfig = appConfig;
        this.checker = checker;
        this.mapper = mapper;
        this.envConfigLoader = loader;
        this.serviceInfoList = Objects.isNull(this.envConfigLoader.getServices())?
                this.appConfig.getServices()
                :this.envConfigLoader.getServices();
    }

    @Scheduled(fixedRateString = "#{@appConfig.check.intervalMs}")
    public void runChecks() {
        List<HealthStatus> results = new ArrayList<>();
        for (ServiceInfo service : serviceInfoList) {
            HealthStatus status = checker.check(service);
            results.add(status);
        }

        // Update in-memory list
        latestStatus.clear();
        latestStatus.addAll(results);

        // Write to JSON file
        try {
            String outputPath = appConfig.getCheck().getOutputDir();
            String fileName = "health-status-"
                    + ZonedDateTime.now().format(
                            DateTimeFormatter.ofPattern(this.appConfig.getCheck().getFileDateFormatter())) + ".json";
            // Create directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Combine directory + filename
            File file = new File(dir, fileName);

            // Write JSON
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, results);
            log.info("Health status written to {}", file.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to write health status JSON", e);
        }
    }
}
