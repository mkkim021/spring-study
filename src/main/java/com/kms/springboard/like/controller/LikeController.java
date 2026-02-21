package com.kms.springboard.like.controller;


import com.kms.springboard.common.dto.ApiResponse;
import com.kms.springboard.like.dto.LikeDto;
import com.kms.springboard.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{boardId}/like")
    public ResponseEntity<ApiResponse<LikeDto>> like(
            @PathVariable Long boardId,
            Authentication auth){
        String userId = auth.getName();
        boolean isliked = likeService.toggleLike(boardId, userId);

        String message;
        if(isliked){
            message = "좋아요를 눌렀습니다.";
        }else{
            message = "좋아요를 취소했습니다.";
        }
        return ResponseEntity.ok(ApiResponse.success(message,null));
    }

    @GetMapping("/{boardId}/like/count")
    public ResponseEntity<ApiResponse<Long>> getLikeCount(
            @PathVariable Long boardId){
        Long likeCount = likeService.getLikeCount(boardId);
        return ResponseEntity.ok(ApiResponse.success("좋아요 개수 조회", likeCount));
    }

    @GetMapping("/like/{boardId}")
    public ResponseEntity<ApiResponse<Page<LikeDto>>> getLikeUsers(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20")int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<LikeDto> users = likeService.getLikeUsers(boardId, pageable);

        return ResponseEntity.ok(ApiResponse.success("좋아요 누른 사람 목록 조회", users));

    }

    @GetMapping("/like/{userId}")
    public ResponseEntity<ApiResponse<Page<LikeDto>>> getLikeBoards(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<LikeDto> userLikeBoards = likeService.getUserLikeBoards(userId, pageable);

        return ResponseEntity.ok(ApiResponse.success("사용자가 좋아요 누른 게시글", userLikeBoards));
    }




}
