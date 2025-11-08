package org.monitor.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfo {
    private String name;
    private String url;
    private String expectedVersion;
}
