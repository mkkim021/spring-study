package com.kms.springboard.comment.repository;


import com.kms.springboard.comment.entity.CommentEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query("SELECT c FROM CommentEntity c WHERE c.board.id = :boardId")
    Page<CommentEntity> findCommentsByBoardId(Long boardId, Pageable pageable);
    Page<CommentEntity> findCommentsByUserId(String userId, Pageable pageable);
}
