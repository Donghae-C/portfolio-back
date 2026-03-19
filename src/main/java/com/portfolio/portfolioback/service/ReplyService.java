package com.portfolio.portfolioback.service;

import com.portfolio.portfolioback.dto.ReplyInboundDTO;
import com.portfolio.portfolioback.dto.ReplyOutboundDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ReplyService {
    List<ReplyOutboundDTO> getReplyList(Long boardId);
    void writeReply(ReplyInboundDTO replyInboundDTO);
    void deleteReply(ReplyInboundDTO replyInboundDTO);

}
