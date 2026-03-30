package com.portfolio.portfolioback.service;

import com.portfolio.portfolioback.entity.RefreshTokens;
import com.portfolio.portfolioback.entity.Users;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface RefreshTokenService {
    RefreshTokens saveToken(Users user, String refreshToken);
    String reissueAccessToken(String refreshToken);
    void reissueAccessTokenByCookie(String refreshToken, HttpServletResponse response);
    void logout(Long userId);
    void saveLoginCode(String code, Long userId);
    Map<String, String> issueTokensByCode(String code);
    Map<String, String> issueGuestToken();
}
