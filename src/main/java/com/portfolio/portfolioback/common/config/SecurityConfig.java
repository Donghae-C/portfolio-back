package com.portfolio.portfolioback.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.portfolioback.common.filter.JWTFilter;
import com.portfolio.portfolioback.common.oauth.CustomOidcUserService;
import com.portfolio.portfolioback.common.oauth.OAuth2LoginSuccessHandler;
import com.portfolio.portfolioback.common.security.CustomAccessDeniedHandler;
import com.portfolio.portfolioback.common.security.CustomAuthenticationEntryPoint;
import com.portfolio.portfolioback.common.security.CustomAuthorizationRequestResolver;
import com.portfolio.portfolioback.common.util.JWTUtil;
import com.portfolio.portfolioback.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomOidcUserService customOidcUserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JWTUtil jwtUtil;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        log.info("bCryptPasswordEncoder called...");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        CustomAuthorizationRequestResolver customResolver = new CustomAuthorizationRequestResolver(clientRegistrationRepository);

        // csrf disable (csrf공격을 방어하기 위한 토큰 주고 받는 부분을 비활성화)
        http.csrf((auth) -> auth.disable());
        // http basic 인증 방식 disable
        http.httpBasic((auth) -> auth.disable());
        // Form 로그인 방식 disable -> React, JWT 인증 방식으로 변경
        // disable 설정하면 security의 UsernamePasswordAuthenticationFilter 비활성화
        http.formLogin((auth) -> auth.disable());

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth.requestMatchers("/api/user/auth/me", "/api/board/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().permitAll());

        // 모두 허용 (임시)
        //http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler));

        // 경로별 인가 작업 (필요한 거 추가!)
        /*
        http.authorizeHttpRequests(auth -> auth
            // OAuth 시작 / 콜백 관련 경로 허용
            .requestMatchers("/", "/login/**", "/oauth2/**").permitAll()

            // 테스트용 공개 API
            .requestMatchers("/index", "/api/user", "/api/user/**", "/posts/**").permitAll()

            // swagger 허용
            .requestMatchers(
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/swagger-resources/**",
                    "/webjars/**"
            ).permitAll()

            // 관리자
            .requestMatchers("/api/admin/**").hasRole("ADMIN")

            // 나머지는 인증 필요
            .anyRequest().authenticated()
        );

         */






        http.oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization.authorizationRequestResolver(customResolver))
                .userInfoEndpoint(userInfo -> userInfo
                        .oidcUserService(customOidcUserService)
                )
                .successHandler(oAuth2LoginSuccessHandler)
        );
        /*
                // 로그인 실패 시 처리
                .failureHandler(oAuth2LoginFailureHandler)
        );
*/

        // JWT 검증 필터
        // 이후 요청에서 access token 검사할 때 사용
        http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://dh-portfolio-delta.vercel.app"));

        configuration.setAllowedMethods(Collections.singletonList("*"));

        configuration.setAllowedHeaders(Collections.singletonList("*"));

        configuration.setAllowCredentials(true);

        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
