package com.portfolio.portfolioback.repository;

import com.portfolio.portfolioback.entity.LoginCodes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoginCodeRepository extends JpaRepository<LoginCodes, Long> {

    Optional<LoginCodes> findByCode(String code);
}

