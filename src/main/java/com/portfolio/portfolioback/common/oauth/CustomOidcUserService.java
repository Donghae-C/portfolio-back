package com.portfolio.portfolioback.common.oauth;

import com.portfolio.portfolioback.common.enumtype.UserRole;
import com.portfolio.portfolioback.common.security.CustomOidcUser;
import com.portfolio.portfolioback.entity.Users;
import com.portfolio.portfolioback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOidcUserService extends OidcUserService {
    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        /*
        log.info("===== CustomOidcUserService loadUser =====");

        // 어떤 클라이언트 등록 정보인지 확인
        log.info("registrationId = {}", userRequest.getClientRegistration().getRegistrationId());

        // access token 자체 확인
        log.info("accessToken = {}", userRequest.getAccessToken().getTokenValue());

        // access token scope 확인
        log.info("scopes = {}", userRequest.getAccessToken().getScopes());

        // userInfo endpoint 설정값 확인
        log.info("userInfoUri = {}",
                userRequest.getClientRegistration()
                        .getProviderDetails()
                        .getUserInfoEndpoint()
                        .getUri());

        // userInfo는 null일 수 있음
        log.info("userInfo = {}", oidcUser.getUserInfo());

        // 핵심: id token 안의 claim 확인
        log.info("idToken claims = {}", oidcUser.getIdToken().getClaims());

        // attributes는 idToken + userInfo 기반으로 합쳐져서 보일 수 있음
        log.info("attributes = {}", oidcUser.getAttributes());

        // 자주 쓰는 값 직접 확인
        log.info("sub = {}", oidcUser.getSubject());
        log.info("email = {}", oidcUser.getEmail());
        log.info("fullName = {}", oidcUser.getFullName());
        */

        String email = oidcUser.getEmail();
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oidcUser.getSubject();


        Users user = userRepository.findByProviderAndProviderId(provider, providerId).orElseGet(()->{
            String uuid  = UUID.randomUUID().toString().toLowerCase(Locale.ROOT).substring(0,6);
            String userName = "익명-" + uuid;

            Users newUser = Users.builder()
                    .email(email)
                    .userName(userName)
                    .provider(provider)
                    .providerId(providerId)
                    .role(UserRole.ROLE_USER)
                    .build();
            return userRepository.save(newUser);
        });

        return new CustomOidcUser(user, oidcUser);
    }
}
