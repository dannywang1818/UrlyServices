package com.urly.urlyservices.security.jwt;

import com.urly.urlyservices.security.config.WebSecurityJWTProperties;
import com.urly.urlyservices.security.userdetail.UserDetailsImpl;
import com.urly.urlyservices.security.userdetail.UserDetailsServiceImpl;
import com.urly.urlyservices.util.security.CookieUtils;
import com.urly.urlyservices.util.security.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private WebSecurityJWTProperties webSecurityJWTProperties;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("internal");
            String token = parseJWT(request);
            if (token != null && JWTUtils.validateJWTToken(token, webSecurityJWTProperties.getSecretKey())) {
                log.info("jwt checked");
                Long id = JWTUtils.getIdFromJWTToken(token, webSecurityJWTProperties.getSecretKey());

                UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserById(id);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String parseJWT(HttpServletRequest request) {
        Optional<Cookie> cookieOptional = CookieUtils.getCookie(
                request,
                webSecurityJWTProperties.getAccessTokenCookieName()
        );
        return cookieOptional.map(Cookie::getValue).orElse(null);
    }
}
