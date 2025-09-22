package com.kms.springboard.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotBlank(message = "댓글 내용을 입력해주세요")
    private String content;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String writer;
    @NotNull(message = "게시글 ID가 필요합니다")
    private Long boardId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long memberId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;



}
