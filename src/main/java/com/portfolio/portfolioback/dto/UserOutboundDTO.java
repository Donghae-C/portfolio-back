package com.portfolio.portfolioback.dto;

import com.portfolio.portfolioback.common.enumtype.UserRole;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOutboundDTO {
    private Long UserId;
    private UserRole UserRole;
    private String userName;
}
