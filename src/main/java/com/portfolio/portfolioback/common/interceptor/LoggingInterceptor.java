package com.portfolio.portfolioback.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        String ua = request.getHeader("User-Agent");

        log.info("접속 로그 >> {} {} {}", ip, method, uri);
        log.info("UA >> {}", ua);
        return true;
    }
}
