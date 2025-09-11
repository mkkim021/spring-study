package com.kms.springboard.comment.service;


import com.kms.springboard.comment.dto.CommentCreateRequest;
import com.kms.springboard.comment.dto.CommentDto;

public interface CommentService {
    CommentDto createComment(CommentCreateRequest request);

}
