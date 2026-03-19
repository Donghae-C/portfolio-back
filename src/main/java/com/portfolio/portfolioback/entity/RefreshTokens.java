package com.portfolio.portfolioback.entity;

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
public class RefreshTokens {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime expireTime;

    @PrePersist // DB insert 직전에 실행됨
    public void prePersist() {
        if (startTime == null) { // 혹시 Hibernate가 안채웠을 경우 대비
            startTime = LocalDateTime.now();
        }

        expireTime = startTime.plusDays(14); // 만료시간 계산
    }
}
