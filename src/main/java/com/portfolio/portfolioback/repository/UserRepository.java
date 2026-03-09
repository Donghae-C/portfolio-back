package com.portfolio.portfolioback.repository;

import com.portfolio.portfolioback.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
