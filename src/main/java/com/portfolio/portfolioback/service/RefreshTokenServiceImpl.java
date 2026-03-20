package com.portfolio.portfolioback.service;

import com.portfolio.portfolioback.common.enumtype.UserRole;
import com.portfolio.portfolioback.common.exception.ErrorCode;
import com.portfolio.portfolioback.common.exception.MyPortFolioException;
import com.portfolio.portfolioback.common.security.RefreshTokenGenerator;
import com.portfolio.portfolioback.common.util.JWTUtil;
import com.portfolio.portfolioback.common.util.TokenHashUtil;
import com.portfolio.portfolioback.entity.LoginCodes;
import com.portfolio.portfolioback.entity.RefreshTokens;
import com.portfolio.portfolioback.entity.Users;
import com.portfolio.portfolioback.repository.LoginCodeRepository;
import com.portfolio.portfolioback.repository.RefreshTokenRepository;
import com.portfolio.portfolioback.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService{
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginCodeRepository loginCodeRepository;
    private final JWTUtil jWTUtil;
    private final UserRepository userRepository;

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
        log.info("hashedToken: {}", hashedToken);
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

    @Override
    public Map<String, String> issueTokensByCode(String code) {
        LoginCodes codes = loginCodeRepository.findByCode(code).orElseThrow(() -> new MyPortFolioException(ErrorCode.NOT_AUTH));
        if(codes.isExpired()){
            loginCodeRepository.delete(codes);
            return null;
        }
        Users user = userRepository.findById(codes.getUserId()).orElseThrow(() -> new MyPortFolioException(ErrorCode.USER_NOTFOUND));
        String accessToken = jWTUtil.createAccessJwt(user, user.getRole().name(), 1000 * 60 * 10L);
        String refreshToken = RefreshTokenGenerator.generate();
        String hashedRefreshToken = TokenHashUtil.sha256(refreshToken);
        saveToken(user, hashedRefreshToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("refreshToken", refreshToken);
        tokens.put("accessToken", accessToken);

        loginCodeRepository.delete(codes);

        return tokens;
    }

    @Override
    public void saveLoginCode(String code, Long userId) {
        LoginCodes loginCodes = LoginCodes.builder().code(code).userId(userId).build();
        try {
            loginCodeRepository.save(loginCodes);
        } catch (Exception e) {
            throw new MyPortFolioException(ErrorCode.DB_ERROR);
        }
    }
}
