package com.portfolio.portfolioback.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyOutboundDTO {
    private Long replyId;
    private Long userId;
    private String userName;
    private Long boardId;
    private String content;
}
