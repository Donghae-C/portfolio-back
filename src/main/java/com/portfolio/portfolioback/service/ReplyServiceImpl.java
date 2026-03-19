package com.portfolio.portfolioback.service;

import com.portfolio.portfolioback.common.enumtype.UserRole;
import com.portfolio.portfolioback.common.exception.ErrorCode;
import com.portfolio.portfolioback.common.exception.MyPortFolioException;
import com.portfolio.portfolioback.dto.BoardOutboundDTO;
import com.portfolio.portfolioback.dto.ReplyInboundDTO;
import com.portfolio.portfolioback.dto.ReplyOutboundDTO;
import com.portfolio.portfolioback.entity.Boards;
import com.portfolio.portfolioback.entity.Replys;
import com.portfolio.portfolioback.entity.Users;
import com.portfolio.portfolioback.repository.ReplyRepository;
import com.portfolio.portfolioback.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;

    @Override
    public List<ReplyOutboundDTO> getReplyList(Long boardId) {
        List<Replys> replysList = replyRepository.findByBoard_BoardId(boardId);
        List<ReplyOutboundDTO> replyOutboundDTOList = replysList.stream().map(n -> {
            ReplyOutboundDTO dto = toDTO(n);
            return dto;
        }).toList();
        return replyOutboundDTOList;
    }

    @Override
    public void writeReply(ReplyInboundDTO replyInboundDTO) {
        Replys replys = toEntity(replyInboundDTO);
        try {
            replyRepository.save(replys);
        } catch (Exception e) {
            throw new MyPortFolioException(ErrorCode.BOARD_DB_ERROR);
        }
    }

    @Override
    public void deleteReply(ReplyInboundDTO replyInboundDTO) {
        Replys replys = replyRepository.findById(replyInboundDTO.getReplyId()).orElseThrow(() -> new MyPortFolioException(ErrorCode.REPLY_NOTFOUND));
        Users user = userRepository.findById(replyInboundDTO.getUserId()).orElseThrow(() -> new MyPortFolioException(ErrorCode.USER_NOTFOUND));
        if(!replyInboundDTO.getUserId().equals(replys.getUser().getUserId()) || !UserRole.ROLE_ADMIN.equals(user.getRole())) {
            throw new MyPortFolioException(ErrorCode.NOT_AUTH);
        }
        try {
            replyRepository.deleteById(replys.getReplyId());
        } catch (Exception e) {
            throw new MyPortFolioException(ErrorCode.BOARD_DB_ERROR);
        }
    }

    private Replys toEntity(ReplyInboundDTO replyInboundDTO) {
        Users user = Users.builder().userId(replyInboundDTO.getUserId()).build();
        Boards board = Boards.builder().boardId(replyInboundDTO.getBoardId()).build();
        Replys reply = Replys.builder()
                .user(user)
                .board(board)
                .content(replyInboundDTO.getContent())
                .build();
        return reply;
    }

    private ReplyOutboundDTO toDTO(Replys reply) {
        ReplyOutboundDTO replyOutboundDTO = ReplyOutboundDTO.builder()
                .replyId(reply.getReplyId())
                .userId(reply.getUser().getUserId())
                .userName(reply.getUser().getUserName())
                .boardId(reply.getBoard().getBoardId())
                .content(reply.getContent())
                .build();
        return replyOutboundDTO;
    }
}
