package com.kms.springboard.like.entity;


import com.kms.springboard.member.entity.MemberEntity;
import com.kms.springboard.post.entity.BoardEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "likes",
        uniqueConstraints ={
                @UniqueConstraint(
                        name = "uk_likes_board_user",
                        columnNames = {"board_id", "user_id"}

                )
        },
        indexes = {
                @Index(name = "idx_likes_board_id", columnList = "board_id"),
                @Index(name = "idx_likes_user_id", columnList = "user_id")
        }
)
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private MemberEntity member;

    @Column(name = "user_id",nullable = false)
    private String userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
