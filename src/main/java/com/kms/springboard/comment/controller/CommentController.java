package com.kms.springboard.comment.controller;



import com.kms.springboard.comment.dto.CommentDto;
import com.kms.springboard.comment.service.CommentService;
import com.kms.springboard.common.dto.ApiResponse;
import com.kms.springboard.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/boards/{boardId}")
public class CommentController {
    private final CommentService commentService;
    private final MemberRepository memberRepository;


    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CommentDto>> createComment(
            @PathVariable Long boardId,
            @Valid @RequestBody CommentDto request,
            Authentication auth) {
        try{
            if(auth == null || !auth.isAuthenticated()||auth instanceof AnonymousAuthenticationToken) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증이 필요합니다"));
            }
            CommentDto savedComment = commentService.createComment(boardId,request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("댓글 작성 완료",savedComment));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }

    }

    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<Page<CommentDto>>> getCommentsByBoardId(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10")@Positive @Max(100)int size,
            Authentication auth) {


        if (auth == null || !auth.isAuthenticated()|| auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다"));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentDto> commentsByBoardId = commentService.findByBoardId(boardId, pageable);

        return ResponseEntity.ok(ApiResponse.success("해당 게시글 댓글 조회 성공", commentsByBoardId));


    }
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Page<CommentDto>>> getCommentsByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10")@Positive@Max(100) int size,
            Authentication auth){
        log.info("getCommentsByUserId {}", userId);

        if (auth == null || !auth.isAuthenticated()|| auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증이 필요합니다"));
        }

        if(!auth.getName().equals(userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("해당 사용자만 조회할 수 있습니다"));

        }
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentDto> commentsByMemberId = commentService.findByUserId(userId, pageable);

        return ResponseEntity.ok(ApiResponse.success("해당 유저가 작성한 댓글 조회 성공", commentsByMemberId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDto>> updateComment(
            @RequestBody CommentDto commentDto,
            @PathVariable Long commentId,
            Authentication auth) {
        try {
            if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증이 필요합니다."));

            }
            CommentDto updated = commentService.updateComment(commentId, commentDto);
            return ResponseEntity.ok(ApiResponse.success("댓글 수정 완료", updated));
        } catch (AccessDeniedException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("작성자만 수정할 수 있습니다."));
        } catch (EntityNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("해당 댓글은 존재하지 않습니다"));
        }

    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId, Authentication auth) {
        try {
            commentService.deleteComment(commentId, auth);
            return ResponseEntity.ok(ApiResponse.success("댓글 삭제 완료", null));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("해당 댓글이 존재하지 않습니다"));
        }
    }


}
