package com.portfolio.portfolioback.service;

import com.portfolio.portfolioback.dto.BoardInboundDTO;
import com.portfolio.portfolioback.dto.BoardOutboundDTO;
import com.portfolio.portfolioback.entity.Boards;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardService {
    Page<BoardOutboundDTO> findAllBoards(int pageNo, int pageSize);
    BoardOutboundDTO findBoardById(Long boardId);
    void writeBoard(BoardInboundDTO boardInboundDTO);
    void updateBoard(BoardInboundDTO boardInboundDTO);
    void deleteBoard(BoardInboundDTO boardInboundDTO);
}
