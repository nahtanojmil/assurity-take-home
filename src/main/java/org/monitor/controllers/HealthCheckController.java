package org.monitor.controllers;

import lombok.extern.slf4j.Slf4j;
import org.monitor.entities.HealthStatus;
import org.monitor.entities.ServiceInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.time.ZonedDateTime;

/**
 * Controller to do rest calls for services defined in yaml file.
 */
@Component
@Slf4j
public class HealthCheckController {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Method to ping each service info defined in applcication.yml.
     *
     * @param service object defined in yml
     * @return HealthStatus Object to write into JSON
     */
    public HealthStatus check(ServiceInfo service) {
        long start = System.currentTimeMillis();
        boolean available;
        String version = "unknown";

        try {
            var response = restTemplate.getForObject(service.getUrl(), String.class);
            available = response != null;
            version = service.getExpectedVersion();
        } catch (Exception e) {
            available = false;
            version = "unreachable";
        }

        long latency = System.currentTimeMillis() - start;
        HealthStatus status = new HealthStatus(service.getName(), available, version, latency, ZonedDateTime.now());
        log.info("Checked {}: available={}, latency={}ms", service.getName(), available, latency);
        return status;
    }
}
