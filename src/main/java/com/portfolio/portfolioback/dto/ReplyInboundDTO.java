package com.portfolio.portfolioback.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyInboundDTO {
    private Long replyId;
    private Long userId;
    private Long boardId;
    private String content;
}
