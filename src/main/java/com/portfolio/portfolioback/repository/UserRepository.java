package com.portfolio.portfolioback.repository;

import com.portfolio.portfolioback.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByProviderAndProviderId(String provider, String providerId);
}
