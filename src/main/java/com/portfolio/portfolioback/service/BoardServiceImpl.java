package com.portfolio.portfolioback.service;

import com.portfolio.portfolioback.common.enumtype.UserRole;
import com.portfolio.portfolioback.common.exception.ErrorCode;
import com.portfolio.portfolioback.common.exception.MyPortFolioException;
import com.portfolio.portfolioback.dto.BoardInboundDTO;
import com.portfolio.portfolioback.dto.BoardOutboundDTO;
import com.portfolio.portfolioback.entity.Boards;
import com.portfolio.portfolioback.entity.Users;
import com.portfolio.portfolioback.repository.BoardRepository;
import com.portfolio.portfolioback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;


    @Override
    public Page<BoardOutboundDTO> findAllBoards(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by(Sort.Direction.DESC, "boardId"));
        Page<Boards> allBoards = boardRepository.findAll(pageable);
        Page<BoardOutboundDTO> outDtos =  allBoards.map(n -> {
            BoardOutboundDTO dto = toDTO(n);
            return dto;
        });
        return outDtos;
    }

    @Override
    public BoardOutboundDTO findBoardById(Long boardId) {
        Boards boards = boardRepository.findById(boardId).orElseThrow(() -> new MyPortFolioException(ErrorCode.BOARD_NOTFOUND));
        BoardOutboundDTO dto = toDTO(boards);
        return dto;
    }

    @Override
    public void writeBoard(BoardInboundDTO boardInboundDTO) {
        Boards boards = toEntity(boardInboundDTO);

        try {
            boardRepository.save(boards);
        } catch (Exception e) {
            throw new MyPortFolioException(ErrorCode.BOARD_DB_ERROR);
        }
    }

    @Override
    public void updateBoard(BoardInboundDTO boardInboundDTO) {
        Boards boards = authCheck(boardInboundDTO);
        boards.setTitle(boardInboundDTO.getTitle());
        boards.setContent(boardInboundDTO.getContent());
        try {
            boardRepository.save(boards);
        } catch (Exception e) {
            throw new MyPortFolioException(ErrorCode.BOARD_DB_ERROR);
        }
    }

    @Override
    public void deleteBoard(BoardInboundDTO boardInboundDTO) {
        authCheck(boardInboundDTO);
        try {
            boardRepository.deleteById(boardInboundDTO.getBoardId());
        } catch (Exception e) {
            throw new MyPortFolioException(ErrorCode.BOARD_DB_ERROR);
        }
    }

    private Boards toEntity(BoardInboundDTO boardInboundDTO) {
        Users user = Users.builder().userId(boardInboundDTO.getUserId()).build();
        Boards boards = Boards.builder()
                .users(user)
                .title(boardInboundDTO.getTitle())
                .content(boardInboundDTO.getContent())
                .build();
        return boards;
    }

    private BoardOutboundDTO toDTO(Boards boards) {
        Users user = userRepository.findById(boards.getUsers().getUserId()).orElse(null);
        BoardOutboundDTO boardOutboundDTO = BoardOutboundDTO.builder()
                .boardId(boards.getBoardId())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .title(boards.getTitle())
                .content(boards.getContent())
                .createdAt(boards.getCreatedAt())
                .updatedAt(boards.getUpdatedAt())
                .isPrivate(boards.isPrivate())
                .build();
        return boardOutboundDTO;
    }

    private Boards authCheck(BoardInboundDTO boardInboundDTO) {
        Boards boards = boardRepository.findById(boardInboundDTO.getBoardId()).orElseThrow(() -> new MyPortFolioException(ErrorCode.BOARD_NOTFOUND));
        Users user = userRepository.findById(boardInboundDTO.getUserId()).orElseThrow(() -> new MyPortFolioException(ErrorCode.USER_NOTFOUND));
        if(!boardInboundDTO.getBoardId().equals(boards.getBoardId()) || !UserRole.ROLE_ADMIN.equals(user.getRole())) {
            throw new MyPortFolioException(ErrorCode.NOT_AUTH);
        }
        return boards;
    }
}
