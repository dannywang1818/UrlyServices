package com.urly.urlyservices.security.config;

import com.urly.urlyservices.security.handler.LogoutSuccessHandlerImpl;
import com.urly.urlyservices.security.jwt.AuthEntryPointJwt;
import com.urly.urlyservices.security.jwt.AuthTokenFilter;
import com.urly.urlyservices.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.urly.urlyservices.security.oauth2.Oauth2SuccessHandlerImpl;
import com.urly.urlyservices.security.userdetail.UserDetailsServiceImpl;
import com.urly.urlyservices.service.OAuth2UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private WebSecurityJWTProperties webSecurityJWTProperties;

    @Autowired
    private AuthEntryPointJwt authEntryPointJwt;

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DefaultOAuth2UserService defaultOAuth2UserService() {
        return new DefaultOAuth2UserService();
    }

    @Autowired
    private Oauth2SuccessHandlerImpl oauth2SuccessHandler;

    @Autowired
    private OAuth2UserServiceImpl oAuth2UserService;

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        super.configure(auth);
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://127.0.0.1:3000"));
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().configurationSource(configurationSource()).and().csrf().disable();

        http.exceptionHandling()
                .authenticationEntryPoint(authEntryPointJwt)
                .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .formLogin()
                .disable()
            .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
                .antMatchers("/urly/shorten").hasAuthority("USER")
                .antMatchers("/redirect/*").permitAll()
                .antMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                    .userInfoEndpoint()
                        .userService(defaultOAuth2UserService())
                        .oidcUserService(oAuth2UserService)
                    .and()
                    .authorizationEndpoint()
                        .baseUri("/oauth2/authorize")
                        .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                    .and()
                    .redirectionEndpoint()
                        .baseUri("/oauth2/callback/*")
                    .and()
                    .successHandler(oauth2SuccessHandler);

        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout"))
                .logoutSuccessHandler(logoutSuccessHandler)
                .deleteCookies(webSecurityJWTProperties.getAccessTokenCookieName())
                .deleteCookies("JSESSIONID")
                .permitAll();
    }
}
