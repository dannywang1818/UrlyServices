package com.urly.urlyservices.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "urly.local.cache")
@Data
public class LocalCacheProperties {

    private Integer keepAliveTime;

}
