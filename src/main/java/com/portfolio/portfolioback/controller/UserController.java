package com.portfolio.portfolioback.controller;

import com.portfolio.portfolioback.common.enumtype.UserRole;
import com.portfolio.portfolioback.common.security.CustomUserDetails;
import com.portfolio.portfolioback.common.util.CookieUtil;
import com.portfolio.portfolioback.dto.LoginCode;
import com.portfolio.portfolioback.service.BusService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final BusService busService;

    @GetMapping("/auth/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        UserRole role = userDetails.getUser().getRole();
        String userName = userDetails.getUser().getUserName();

        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of("userId", userId, "role", role, "userName", userName)
        );
    }

    @GetMapping("/auth/reissue")
    public ResponseEntity<?> reissueToken(HttpServletRequest request){
        String refreshToken = CookieUtil.getCookieValue(request, "refreshToken");
        String accessToken = refreshTokenService.reissueAccessToken(refreshToken);
        Map<String, String> responseMap = Map.of("accessToken", accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<?> reissueTokenByCookie(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = CookieUtil.getCookieValue(request, "refreshToken");
        refreshTokenService.reissueAccessTokenByCookie(refreshToken, response);
        Map<String, String> responseMap = Map.of("message", "쿠키로 재발급 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getUser().getUserId();
        refreshTokenService.logout(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/auth/exchange")
    public ResponseEntity<?> exchangeToken(@RequestBody LoginCode code){
        Map<String, String> tokens = refreshTokenService.issueTokensByCode(code.getCode());
        return ResponseEntity.status(HttpStatus.OK).body(tokens);
    }

    @GetMapping("/auth/guest")
    public ResponseEntity<?> getGuest(){
        Map<String, String> map = refreshTokenService.issueGuestToken();
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    @GetMapping("/bus")
    public ResponseEntity<?> getBus(@RequestParam String stationCode, String busCode){
        Map<String, String> busInfo = busService.getBusInfo(stationCode, busCode);
        return ResponseEntity.status(HttpStatus.OK).body(busInfo);
    }

    @GetMapping("/bus/list")
    public ResponseEntity<?> getBusList(@RequestParam String stationCode){
        Map<String, Long> busList = busService.getBusList(stationCode);
        return ResponseEntity.status(HttpStatus.OK).body(busList);
    }
}
