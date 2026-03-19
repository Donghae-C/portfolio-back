package com.portfolio.portfolioback.repository;

import com.portfolio.portfolioback.entity.RefreshTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long> {
    void deleteByUser_UserId(Long userUserId);

    Optional<RefreshTokens> findByToken(String token);
}
