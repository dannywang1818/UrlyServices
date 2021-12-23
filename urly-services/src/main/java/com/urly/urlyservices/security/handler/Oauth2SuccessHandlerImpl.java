package com.urly.urlyservices.security.oauth2;

import com.nimbusds.jwt.JWT;
import com.urly.urlyservices.security.config.WebSecurityCookieProperties;
import com.urly.urlyservices.security.config.WebSecurityJWTProperties;
import com.urly.urlyservices.security.config.WebSecurityOAuth2Properties;
import com.urly.urlyservices.util.security.CookieUtils;
import com.urly.urlyservices.util.security.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Component
public class Oauth2SuccessHandlerImpl extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    private WebSecurityJWTProperties webSecurityJWTProperties;

    @Autowired
    private WebSecurityCookieProperties webSecurityCookieProperties;

    @Autowired
    private WebSecurityOAuth2Properties webSecurityOAuth2Properties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("oauth2 handler");

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.info("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        String token = JWTUtils.generateJWTToken(
                authentication,
                webSecurityJWTProperties.getTokenExpiration(),
                webSecurityJWTProperties.getSecretKey());

        log.info("cookie: " + URLEncoder.encode(token, StandardCharsets.UTF_8));
        response.addHeader(HttpHeaders.SET_COOKIE, CookieUtils.createCookieString(
                webSecurityJWTProperties.getAccessTokenCookieName(),
                token,
                webSecurityCookieProperties.getCookieExpirationSeconds()
        ));
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, WebSecurityOAuth2Properties.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        log.info("redirect uri :" + redirectUri.get());

        if(!isAuthorizedRedirectUri(redirectUri.get())) {
            throw new RuntimeException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        return redirectUri.orElse(getDefaultTargetUrl());
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        for(String uriString : webSecurityOAuth2Properties.getAUTHORIZED_URI()) {
            URI authorizedUri = URI.create(uriString);
            if(authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) &&
            authorizedUri.getPort() == clientRedirectUri.getPort()) {
                return true;
            }
        }
        return false;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

}
