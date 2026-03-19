package com.portfolio.portfolioback.common.oauth;

import com.portfolio.portfolioback.common.security.CustomOidcUser;
import com.portfolio.portfolioback.common.util.JWTUtil;
import com.portfolio.portfolioback.common.security.RefreshTokenGenerator;
import com.portfolio.portfolioback.common.util.TokenHashUtil;
import com.portfolio.portfolioback.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

//이 클래스에서 JWT를 생성 및 응답바디나 쿠키에 내려보냄
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    @Value("${spring.front.url}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOidcUser oidcUser = (CustomOidcUser) authentication.getPrincipal();

        Long userId =  oidcUser.getUserId();
        String role = oidcUser.getRole().name();

        log.info("userId = {}, role = {}", userId, role);

        String accessJwt = jwtUtil.createAccessJwt(oidcUser.getUser(), role, 1000 * 60 * 10L);//10분
        String refreshToken = RefreshTokenGenerator.generate();
        String hashedRefreshToken = TokenHashUtil.sha256(refreshToken);

        refreshTokenService.saveToken(oidcUser.getUser(), hashedRefreshToken);

        Cookie cookieAccessToken = new Cookie("Authorization", accessJwt);
        cookieAccessToken.setPath("/");
        cookieAccessToken.setHttpOnly(true);
        cookieAccessToken.setSecure(false);//일단 로컬환경용 테스트
        cookieAccessToken.setMaxAge(60 * 10);

        Cookie cookieRefreshToken = new Cookie("refreshToken", refreshToken); //평문 refreshToken 담기. 해시는 DB에 저장
        cookieRefreshToken.setPath("/");
        cookieRefreshToken.setHttpOnly(true);
        cookieRefreshToken.setMaxAge(60 * 60 * 24 * 14);
        cookieRefreshToken.setSecure(false);//일단 로컬환경용 테스트라 https불가. 나중에 true로 수정예정

        response.addCookie(cookieAccessToken);
        response.addCookie(cookieRefreshToken);


        log.info("==== 로그인 성공 ====");
        log.info("principal class = {}", authentication.getPrincipal().getClass().getName());
        response.sendRedirect(frontUrl);
    }
}
