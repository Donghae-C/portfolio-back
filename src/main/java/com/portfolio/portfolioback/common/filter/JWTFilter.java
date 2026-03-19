package com.portfolio.portfolioback.common.filter;

import com.portfolio.portfolioback.common.enumtype.UserRole;
import com.portfolio.portfolioback.common.security.CustomUserDetails;
import com.portfolio.portfolioback.common.util.JWTUtil;
import com.portfolio.portfolioback.entity.Users;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;  // token 생성 + 검증
    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*
        // request에서 Authorization 헤더 찾기
        String authorization= request.getHeader("Authorization");

        // Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            System.out.println("token null");
            filterChain.doFilter(request, response);

            // 조건이 해당되면 메소드 종료 (필수)
            return;
        }

        System.out.println("authorization now");
        // Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        // 토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            System.out.println("token expired");
            filterChain.doFilter(request, response);

            // 조건이 해당되면 메소드 종료 (필수)
            return;
        }

         */
        String token = null;
        // 2. 쿠키 바구니에서 "Authorization" 찾기
        Cookie[] cookies = request.getCookies();
        System.out.println(cookies);
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // 쿠키 이름이 "Authorization"인 것을 찾음
                if ("Authorization".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }


        if (token == null) {
            System.out.println("token null");
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("token = " + token);
        System.out.println("authorization now");

        if (jwtUtil.isExpired(token)) {
            System.out.println("token expired");
            filterChain.doFilter(request, response);
            return;
        }


        // 토큰에서 username과 role 획득
        Long userId = jwtUtil.getUserId(token);
        String role = jwtUtil.getRole(token);
        String userName = jwtUtil.getUsername(token);

        // userEntity를 생성하여 값 set
        Users user = Users.builder()
                .userId(userId)
                .role(UserRole.valueOf(role))
                .userName(userName)
                .build();

        // UserDetails에 유저 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // spring security 인증 토큰 생성
        Authentication authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        // 세션에 사용자 등록 - 세션이 만들어짐.
        SecurityContextHolder.getContext().setAuthentication(authToken);
        System.out.println("authToken = " + authToken);
        filterChain.doFilter(request, response);
    }
}
