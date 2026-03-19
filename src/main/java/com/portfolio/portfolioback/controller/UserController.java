package com.portfolio.portfolioback.controller;

import com.portfolio.portfolioback.common.enumtype.UserRole;
import com.portfolio.portfolioback.common.security.CustomUserDetails;
import com.portfolio.portfolioback.common.util.CookieUtil;
import com.portfolio.portfolioback.dto.LoginCode;
import com.portfolio.portfolioback.service.RefreshTokenService;
import com.portfolio.portfolioback.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/auth/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("user: {}", userDetails.getUser().getUserId());
        Long userId = userDetails.getUser().getUserId();
        UserRole role = userDetails.getUser().getRole();
        String userName = userDetails.getUser().getUserName();

        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of("userId", userId, "role", role, "userName", userName)
        );
    }

    @GetMapping("/auth/reissue")
    public ResponseEntity<?> reissueToken(HttpServletRequest request){
        log.info("reissueToken");
        String refreshToken = CookieUtil.getCookieValue(request, "refreshToken");
        log.info("refreshToken: {}", refreshToken);
        String accessToken = refreshTokenService.reissueAccessToken(refreshToken);
        Map<String, String> responseMap = Map.of("accessToken", accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<?> reissueTokenByCookie(HttpServletRequest request, HttpServletResponse response){
        log.info("reissueToken");
        String refreshToken = CookieUtil.getCookieValue(request, "refreshToken");
        log.info("refreshToken: {}", refreshToken);
        refreshTokenService.reissueAccessTokenByCookie(refreshToken, response);
        Map<String, String> responseMap = Map.of("message", "쿠키로 재발급 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails userDetails){
        log.info("logout");
        Long userId = userDetails.getUser().getUserId();
        refreshTokenService.logout(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/auth/exchange")
    public ResponseEntity<?> exchangeToken(@RequestBody LoginCode code){
        log.info("exchangeToken");
        log.info("code: {}", code.getCode());
        Map<String, String> tokens = refreshTokenService.issueTokensByCode(code.getCode());
        return ResponseEntity.status(HttpStatus.OK).body(tokens);
    }


}
