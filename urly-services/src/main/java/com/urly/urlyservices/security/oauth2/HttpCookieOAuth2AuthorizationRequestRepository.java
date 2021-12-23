package com.urly.urlyservices.security.oauth2;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.urly.urlyservices.security.config.WebSecurityCookieProperties;
import com.urly.urlyservices.security.config.WebSecurityOAuth2Properties;
import com.urly.urlyservices.util.security.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    @Autowired
    private WebSecurityCookieProperties webSecurityCookieProperties;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtils.getCookie(request, WebSecurityOAuth2Properties.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        log.info("saved");
        if (authorizationRequest == null) {
            CookieUtils.deleteCookie(
                    request,
                    response,
                    WebSecurityOAuth2Properties.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME
            );

            CookieUtils.deleteCookie(
                    request,
                    response,
                    WebSecurityOAuth2Properties.REDIRECT_URI_PARAM_COOKIE_NAME
            );
            return;
        }

        CookieUtils.addCookie(
                response,
                WebSecurityOAuth2Properties.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtils.serialize(authorizationRequest), webSecurityCookieProperties.getCookieExpirationSeconds()
        );

        String redirectUriAfterLogin = request.getParameter(
                WebSecurityOAuth2Properties.REDIRECT_URI_PARAM_COOKIE_NAME
        );

        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            CookieUtils.addCookie(
                    response,
                    WebSecurityOAuth2Properties.REDIRECT_URI_PARAM_COOKIE_NAME,
                    redirectUriAfterLogin,
                    webSecurityCookieProperties.getCookieExpirationSeconds()
            );
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        log.info("remove auth request old version");
        return loadAuthorizationRequest(request);
    }

//    @Override
//    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
//        log.info("remove auth request new version");
//        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = loadAuthorizationRequest(request);
//        if(oAuth2AuthorizationRequest == null) {
//            return null;
//        }
//        removeAuthorizationRequestCookies(request, response);
//        return oAuth2AuthorizationRequest;
//    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(
                request,
                response,
                WebSecurityOAuth2Properties.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME
        );

        CookieUtils.deleteCookie(
                request,
                response,
                WebSecurityOAuth2Properties.REDIRECT_URI_PARAM_COOKIE_NAME
        );
    }
}

