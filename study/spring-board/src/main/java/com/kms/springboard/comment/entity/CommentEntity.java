package com.kms.springboard.comment.entity;


import com.kms.springboard.member.entity.MemberEntity;
import com.kms.springboard.post.entity.BoardEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.lang.reflect.Member;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardId",nullable = false)
    private BoardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId",nullable = false)
    private MemberEntity member;

    @Column(name = "content")
    private String content;

    @Column(name = "writer")
    private String writer;
}
