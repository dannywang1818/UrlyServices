package com.urly.urlyservices.service;

import com.urly.urlyservices.db.entity.User;
import com.urly.urlyservices.db.repository.UserRepository;
import com.urly.urlyservices.enums.AuthProvider;
import com.urly.urlyservices.security.userdetail.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Slf4j
@Service
public class OAuth2UserServiceImpl extends OidcUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("load Oidc User");
        OidcUser oidcUser = super.loadUser(userRequest);

        try{
            return process(userRequest, oidcUser);
        } catch (AuthenticationException authenticationException) {
            throw new InternalAuthenticationServiceException(
                    authenticationException.getMessage(),
                    authenticationException.getCause()
            );
        }
    }

    public OidcUser process(OidcUserRequest request, OidcUser oidcUser) throws AuthenticationException {
        log.info("process");

        if(StringUtils.isEmpty(oidcUser.getEmail())){
            throw new AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmailAndProvider(
                oidcUser.getEmail(),
                AuthProvider.valueOf(request.getClientRegistration().getRegistrationId().toUpperCase()));
        User user = userOptional.orElseGet(() -> register(request, oidcUser));

        return UserDetailsImpl.create(user, oidcUser);
    }

    public User register(OidcUserRequest request, OidcUser oidcUser) {
        log.info("register");
        User user = new User();

        user.setProvider(
                AuthProvider.valueOf(request.getClientRegistration().getRegistrationId().toUpperCase())
        );
        user.setUsername(oidcUser.getFullName());
        user.setEmail(oidcUser.getEmail());
        user.setImageUrl(oidcUser.getPicture());
        user.setRoles("ROLE_USER");
        return userRepository.save(user);
    }
}
