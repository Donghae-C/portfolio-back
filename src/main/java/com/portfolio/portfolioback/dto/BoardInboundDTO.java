package com.portfolio.portfolioback.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardInboundDTO {
    private Long boardId;
    private Long userId;
    private String title;
    private String content;
    //private boolean isPrivate;
}
