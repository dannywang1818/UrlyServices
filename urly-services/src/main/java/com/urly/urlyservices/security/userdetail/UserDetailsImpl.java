package com.urly.urlyservices.security.userdetail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urly.urlyservices.db.entity.User;
import com.urly.urlyservices.enums.AuthProvider;
import com.urly.urlyservices.exception.RolesConversionException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Setter
@Getter
public class UserDetailsImpl implements UserDetails, OidcUser {

    private Long id;

    private String accountName;

    private String email;

    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    private AuthProvider authProvider;

    private OidcUser oidcUser;

    public UserDetailsImpl(
            Long id,
            String accountName,
            String email,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            AuthProvider authProvider) {
        this.id = id;
        this.accountName = accountName;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.authProvider = authProvider;
    }

    public static UserDetailsImpl create(User user){

        Set<String> roles = Set.of(user.getRoles().split(","));
        List<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getProvider()
        );
    }

    public static UserDetailsImpl create(User user, OidcUser oidcUser) {
        UserDetailsImpl userDetails = UserDetailsImpl.create(user);
        userDetails.setOidcUser(oidcUser);
        return userDetails;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        log.info("get info userimpl");
        return oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        log.info("get token userimpl");
        return oidcUser.getIdToken();
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return getClaims();
    }
}
