package com.portfolio.portfolioback.dto;

import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
}
