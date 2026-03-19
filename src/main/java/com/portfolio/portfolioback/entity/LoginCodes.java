package com.portfolio.portfolioback.entity;

import com.portfolio.portfolioback.common.enumtype.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginCodes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeId;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Long userId;

    @CreationTimestamp()
    private LocalDateTime createdAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(createdAt.plusMinutes(1));
    }
}
