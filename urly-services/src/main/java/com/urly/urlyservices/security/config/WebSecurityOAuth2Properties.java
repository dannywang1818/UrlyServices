package com.urly.urlyservices.security.config;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class WebSecurityOAuth2Properties {

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";

    @Getter
    private final List<String> AUTHORIZED_URI = Arrays.asList(
            "http://127.0.0.1:3000",
            "http://127.0.0.1:3000/login"
    );
}
