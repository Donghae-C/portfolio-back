package com.portfolio.portfolioback.common.security;

import java.security.SecureRandom;
import java.util.Base64;

public class RefreshTokenGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generate(){
        byte[] randomBytes = new byte[64];
        random.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
