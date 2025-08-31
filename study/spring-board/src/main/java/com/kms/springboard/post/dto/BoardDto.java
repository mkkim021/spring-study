package com.kms.springboard.post.dto;

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
    private String content;


}
