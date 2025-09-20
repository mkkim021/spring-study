package com.kms.springboard.comment.service;


import com.kms.springboard.comment.dto.CommentDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentDto createComment(CommentDto request);
    Page<CommentDto> findByBoardId(Long boardId, Pageable pageable);
    Page<CommentDto> findByUserId(String userId, Pageable pageable);

}
