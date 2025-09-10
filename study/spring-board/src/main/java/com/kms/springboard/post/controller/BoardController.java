package com.kms.springboard.post.controller;


import com.kms.springboard.common.dto.ApiResponse;
import com.kms.springboard.post.dto.BoardDto;
import com.kms.springboard.post.service.BoardService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BoardDto>>> getAllBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<BoardDto> boards = boardService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회 성공" , boards));

    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardDto>>getBoard(@PathVariable Long boardId){
        BoardDto board = boardService.findById(boardId);
        return ResponseEntity.ok(ApiResponse.success("게시글 조회 성공", board));

    }

    @PostMapping
    public ResponseEntity<ApiResponse<BoardDto>> createBoard(
            @Valid @RequestBody BoardDto boardDto,
            Authentication authentication){
        boardDto.setWriter(authentication.getName());
        BoardDto saved = boardService.save(boardDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글 작성 완료", saved));
    }
    @PutMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardDto>> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardUpdateRequest request,
            Authentication authentication){
        try{
            if(authentication == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증이 필요합니다"));
            }

            boardService.updateWithPassword(
                    boardId,
                    request.getBoardDto(),
                    request.getPostPassword(),
                    authentication.getName()
            );
            BoardDto updateBoard = boardService.findById((boardId));
            return ResponseEntity.ok(ApiResponse.success("게시글 수정 완료", updateBoard));

        }catch(EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("게시글을 찾을 수 없습니다"));
        }catch(AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("수정 권한이 없습니다"));
        }

    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(
            @PathVariable Long boardId,
            Authentication authentication){
        try{
            if(authentication == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증이 필요합니다"));
            }
            boardService.delete(boardId, authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("게시글 삭제 완료", null));

        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("게시글을 찾을 수 없습니다."));
        }catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("삭제 권한이 없습니다."));
        }
    }



}

@Data
class BoardUpdateRequest {
    @Valid
    private BoardDto boardDto;
    @NotBlank
    private String postPassword;
}
