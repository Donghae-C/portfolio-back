package com.portfolio.portfolioback.repository;

import com.portfolio.portfolioback.entity.Replys;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Replys, Long> {
    List<Replys> findByBoard_BoardId(Long boardBoardId);
}
