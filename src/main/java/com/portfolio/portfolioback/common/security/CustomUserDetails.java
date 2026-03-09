package com.portfolio.portfolioback.common.security;

import com.portfolio.portfolioback.entity.Users;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Users user;

    public CustomUserDetails(Users user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(()->"ROLE_" + user.getRole().toString());   // security 규칙인 ROLE_ 붙여줌
        return authorities;
    }



    //password 사용 안함. null 반환. 애초에 쓰지도 않을거지만 혹시나 OAuth2 말고 기본 로그인으로 기능 확장이 될지 몰라 둠
    @Override
    public @Nullable String getPassword() {
        //return user.getPassword();
        return null;
    }
    //위와 마찬가지.
    @Override
    public String getUsername() {
        //return user.getUsername();
        return null;
    }
}
