package com.kms.springboard.comment.controller;



import com.kms.springboard.comment.dto.CommentDto;
import com.kms.springboard.comment.service.CommentService;
import com.kms.springboard.common.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;


    @PostMapping
    public ResponseEntity<ApiResponse<CommentDto>> createComment(
            @Valid @RequestBody CommentDto request,
            Authentication auth) {
        try{
            if(auth == null || !auth.isAuthenticated()||auth instanceof AnonymousAuthenticationToken) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증이 필요합니다"));
            }
            CommentDto savedComment = commentService.createComment(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("댓글 작성 완료",savedComment));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("게시글을 찾을 수 없습니다"));
        }

    }

    @GetMapping("/boards/{boardId}")
    public ResponseEntity<ApiResponse<Page<CommentDto>>> getCommentByBoardId(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10")int size,
            Authentication auth) {


        if (auth == null || !auth.isAuthenticated()|| auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다"));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentDto> commentsByBoardId = commentService.findByBoardId(boardId, pageable);

        return ResponseEntity.ok(ApiResponse.success("해당 게시글 댓글 조회 성공", commentsByBoardId));


    }


}
