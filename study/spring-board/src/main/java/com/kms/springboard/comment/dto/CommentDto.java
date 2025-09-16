package com.kms.springboard.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private String writer;
    private Long boardId;
}
