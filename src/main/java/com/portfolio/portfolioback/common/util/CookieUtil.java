package com.portfolio.portfolioback.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtil {
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        // 쿠키 자체가 없으면 null 반환
        if (request.getCookies() == null) {
            return null;
        }

        // 원하는 이름의 쿠키를 찾아서 값 반환
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }

        // 못 찾으면 null
        return null;
    }
}
