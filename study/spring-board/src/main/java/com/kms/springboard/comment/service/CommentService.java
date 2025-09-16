package com.kms.springboard.comment.service;


import com.kms.springboard.comment.dto.CommentCreateRequest;
import com.kms.springboard.comment.dto.CommentDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    CommentDto createComment(CommentCreateRequest request);
    Page<CommentDto> findByBoardId(Long boardId, Pageable pageable);
    CommentDto updateComment(Long commentId, CommentCreateRequest request);
    void deleteComment(Long commentId);

}
