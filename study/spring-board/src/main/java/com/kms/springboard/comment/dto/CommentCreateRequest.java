package com.kms.springboard.comment.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {
    @NotBlank(message = "댓글 내용을 입력해주세요")
    private String content;

    @NotNull(message = "게시글 아이디가 필요합니다")
    private Long boardId;


}
