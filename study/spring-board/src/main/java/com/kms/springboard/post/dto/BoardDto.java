package com.kms.springboard.post.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BoardDto {
    private Long id;
    private String title;
    private String writer;
    @NotBlank(message = "게시글 비밀번호를 입력해주세요")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String postPassword;
    private String content;


}
