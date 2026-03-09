package com.portfolio.portfolioback.repository;

import com.portfolio.portfolioback.entity.Boards;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Boards, Long> {
}
