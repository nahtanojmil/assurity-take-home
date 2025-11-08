package org.monitor.configs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.monitor.entities.ServiceInfo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * To read serviceInfo from .env file.
 */
@Component
@Slf4j
public class EnvConfigLoader {

    private final ObjectMapper mapper;
    @Getter
    private List<ServiceInfo> services;

    public EnvConfigLoader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    public void init() {
        try {
            String servicesJson = System.getenv("CHECK_SERVICES_JSON");
            services = mapper.readValue(servicesJson, new TypeReference<List<ServiceInfo>>() {});
            log.info("Service List {}", services.stream().map(ServiceInfo::getName).toList());
        } catch (Exception e) {
            log.error("Failed to parse CHECK_SERVICES_JSON, reverting to yml properties");
        }
    }

}
