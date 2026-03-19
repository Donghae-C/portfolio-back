package com.portfolio.portfolioback.service;

import com.portfolio.portfolioback.common.enumtype.UserRole;
import com.portfolio.portfolioback.common.util.JWTUtil;
import com.portfolio.portfolioback.common.util.TokenHashUtil;
import com.portfolio.portfolioback.entity.RefreshTokens;
import com.portfolio.portfolioback.entity.Users;
import com.portfolio.portfolioback.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService{
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jWTUtil;

    @Override
    public RefreshTokens saveToken(Users user, String refreshToken) {
        refreshTokenRepository.deleteByUser_UserId(user.getUserId());
        RefreshTokens refreshTokens = RefreshTokens.builder()
                .user(user)
                .token(refreshToken)
                .build();
        return refreshTokenRepository.save(refreshTokens);
    }

    @Override
    public String reissueAccessToken(String refreshToken) {


        String accessJwt = createAccessToken(refreshToken);
        log.info("AccessToken: {}", accessJwt);
        return accessJwt;
    }

    @Override
    public void reissueAccessTokenByCookie(String refreshToken, HttpServletResponse response) {

        String accessJwt = createAccessToken(refreshToken);

        Cookie cookieAccessToken = new Cookie("Authorization", accessJwt);
        cookieAccessToken.setPath("/");
        cookieAccessToken.setHttpOnly(true);
        cookieAccessToken.setSecure(false);//일단 로컬환경용 테스트
        cookieAccessToken.setMaxAge(60 * 10);

        log.info("cookieAccessToken: {}", accessJwt);
        response.addCookie(cookieAccessToken);
    }

    @Override
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUser_UserId(userId);
    }

    private String createAccessToken(String refreshToken){
        log.info("토큰 재발급 중");
        //1. 토큰자체가 있는지 확인
        if(refreshToken==null||refreshToken.isBlank()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 없음");
        }
        log.info("2");
        //2. db에 저장되어 있는 토큰인지 확인
        String hashedToken = TokenHashUtil.sha256(refreshToken);
        RefreshTokens dbRefreshToken = refreshTokenRepository.findByToken(hashedToken).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "DB에 저장된 토큰이 아님"));
        log.info("3");
        //3. 만료확인
        if(dbRefreshToken.getExpireTime().isBefore(LocalDateTime.now())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰 만료");
        }

        log.info("테스트중...");

        //4. 토큰엔티티에서 User추출
        Users user = dbRefreshToken.getUser();
        UserRole role = user.getRole();
        log.info("user: {}", user);

        String accessJwt = jWTUtil.createAccessJwt(user, role.toString(), 1000 * 60 * 10L);

        return accessJwt;
    }
}
