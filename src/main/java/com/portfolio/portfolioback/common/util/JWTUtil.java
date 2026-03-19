package com.portfolio.portfolioback.common.util;

import com.portfolio.portfolioback.entity.Users;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;


/**
 * JWT 정보 검증 및 생성
 * (설정된 비밀키(secret key)를 가져와서 token을 생성)
 */
@Component
@Slf4j
public class JWTUtil {

    private SecretKey secretKey;    // Decode한 secret key를 담는 객체

    // application.properties에 있는 미리 Base64로 Encode된 Secret key를 가져온다
    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // userId 검증
    public Long getUserId(String token) {
        log.info("getUserId(String token) called");
        Long result = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", Long.class);
        log.info("getUserId(String token) result = {}", result);
        return result;
    }

    // username 검증
    public String getUsername(String token) {
        log.info("getUsername(String token) called");
        String result = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userName", String.class);
        log.info("getUsername(String token) result = {}", result);
        return result;
    }

    // role 검증
    public String getRole(String token) {
        log.info("getRole(String token) called");
        String result = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
        log.info("getRole(String token) result = {} " , result);
        return result;
    }

    // expired 검증
    public Boolean isExpired(String token) {
        log.info("isExpired(String token) call");
        boolean result = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        log.info("isExpired(String token) result  = {}", result);
        return result;
    }

    // Bearer : JWT 혹은 Oauth에 대한 토큰을 사용
    // claim은 payload에 해당하는 정보
    //public String createJwt(String username, String role, Long expiredMs) {
    public String createAccessJwt(Users user, String role, Long expiredMs) {
        log.info("createJwt  call");
        return Jwts.builder()
                .claim("userId", user.getUserId()) // userId (PK)
                .claim("role", role) // admin or user
                .claim("userName", user.getUserName())
                .issuedAt(new Date(System.currentTimeMillis())) // 현재 로그인 된 시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료시간
                .signWith(secretKey)
                .compact();
    }
}
