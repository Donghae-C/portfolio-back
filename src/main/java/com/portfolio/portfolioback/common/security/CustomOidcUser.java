package com.portfolio.portfolioback.common.security;

import com.portfolio.portfolioback.common.enumtype.UserRole;
import com.portfolio.portfolioback.entity.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOidcUser implements OidcUser {

    private final Users user;
    private final OidcUser oidcUser;

    public CustomOidcUser(Users user, OidcUser oidcUser) {
        this.user = user;
        this.oidcUser = oidcUser;
    }

    public Long getUserId(){
        return user.getUserId();
    }

    public UserRole getRole(){
        return user.getRole();
    }

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oidcUser.getAuthorities();
    }

    @Override
    public String getName() {
        return oidcUser.getName();
    }
}
