package com.portfolio.portfolioback.common.oauth;

import com.portfolio.portfolioback.common.security.CustomOidcUser;
import com.portfolio.portfolioback.common.security.JWTUtil;
import com.portfolio.portfolioback.common.security.RefreshTokenGenerator;
import com.portfolio.portfolioback.common.security.TokenHashUtil;
import com.portfolio.portfolioback.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOidcUser oidcUser = (CustomOidcUser) authentication.getPrincipal();

        Long userId =  oidcUser.getUserId();
        String role = oidcUser.getRole().toString();

        log.info("userId = {}, role = {}", userId, role);

        String accessJwt = jwtUtil.createAccessJwt(oidcUser.getUser(), role, 1000 * 60 * 10L);//10분
        String refreshToken = RefreshTokenGenerator.generate();
        String hashedRefreshToken = TokenHashUtil.sha256(refreshToken);

        response.addHeader("Set-Cookie",
                "refreshToken=" + refreshToken +
                "; HttpOnly" +
                "; Secure" +
                "; Path=/" +
                "; SameSite=Lax" +
                "; Max-Age=1209600");
    }
}
