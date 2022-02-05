package com.urly.urlyservices.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "urly.security.cookie")
@Data

public class WebSecurityCookieProperties {

    // Cookie Properties
    private Integer cookieExpirationSeconds;
}
