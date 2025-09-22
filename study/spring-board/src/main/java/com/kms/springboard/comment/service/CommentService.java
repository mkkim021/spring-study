package com.kms.springboard.comment.service;


import com.kms.springboard.comment.dto.CommentDto;

import com.kms.springboard.comment.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import javax.xml.stream.events.Comment;

public interface CommentService {
    CommentDto createComment(Long boardId,CommentDto request);
    Page<CommentDto> findByBoardId(Long boardId, Pageable pageable);
    Page<CommentDto> findByUserId(String userId, Pageable pageable);
    CommentDto updateComment(Long commentId, CommentDto request);
    void deleteComment(Long commentId, Authentication auth);

}
