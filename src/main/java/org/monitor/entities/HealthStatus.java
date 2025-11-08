package org.monitor.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatus {
    private String serviceName;
    private boolean available;
    private String version;
    private long latencyMs;
    private ZonedDateTime checkedAt;
}
