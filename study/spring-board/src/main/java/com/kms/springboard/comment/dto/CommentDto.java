package com.kms.springboard.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(message = "댓글 내용을 입력해주세요")
    private String content;
    private String writer;
    private Long boardId;
    private Long memberId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



}
