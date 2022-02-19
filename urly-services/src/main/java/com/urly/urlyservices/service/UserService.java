package com.urly.urlyservices.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.urly.urlyservices.db.entity.User;
import com.urly.urlyservices.db.repository.UserRepository;
import com.urly.urlyservices.enums.AuthProvider;
import com.urly.urlyservices.exception.RolesConversionException;
import com.urly.urlyservices.security.config.WebSecurityCookieProperties;
import com.urly.urlyservices.security.config.WebSecurityJWTProperties;
import com.urly.urlyservices.security.userdetail.UserDetailsImpl;
import com.urly.urlyservices.util.security.CookieUtils;
import com.urly.urlyservices.util.security.JWTUtils;
import com.urly.urlyservices.vo.request.LoginRequest;
import com.urly.urlyservices.vo.request.SignupRequest;
import com.urly.urlyservices.vo.response.LoginResponse;
import com.urly.urlyservices.vo.response.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    @Autowired
    private WebSecurityCookieProperties webSecurityCookieProperties;

    @Autowired
    private WebSecurityJWTProperties webSecurityJWTProperties;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        if(authentication == null) {
            return new ResponseEntity<>(new MessageResponse("Username/Password Not Match"), HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JWTUtils.generateJWTToken(
                authentication,
                webSecurityJWTProperties.getTokenExpiration(),
                webSecurityJWTProperties.getSecretKey()
                );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(
                HttpHeaders.SET_COOKIE,
                CookieUtils.createCookieString(
                        webSecurityJWTProperties.getAccessTokenCookieName(),
                        token,
                        webSecurityCookieProperties.getCookieExpirationSeconds()
                        )
        );

        return ResponseEntity
                .ok()
                .headers(responseHeaders)
                .body(new LoginResponse(
                        userDetails.getId(),
                        userDetails.getAccountName(),
                        userDetails.getEmail()
                ));
    }

    public ResponseEntity<?> signup(SignupRequest signupRequest) {

        if(userRepository.existsByEmail(signupRequest.getEmail())) {
            return new ResponseEntity<>(new MessageResponse("Email Registered"), HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRoles(signupRequest.getRoles());
        user.setProvider(AuthProvider.LOCAL);

        userRepository.save(user);

        log.info("saved new user");

        LoginRequest loginRequest = new LoginRequest(signupRequest.getEmail(), signupRequest.getPassword());

        return login(loginRequest);
    }
}
