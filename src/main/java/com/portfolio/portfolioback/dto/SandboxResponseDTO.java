package com.portfolio.portfolioback.dto;

import com.portfolio.portfolioback.common.enumtype.SandboxStatus;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SandboxResponseDTO {
    private SandboxStatus status;
    private String result;
    private String error;
}
