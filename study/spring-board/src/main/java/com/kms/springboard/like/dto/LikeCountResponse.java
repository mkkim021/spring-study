package com.kms.springboard.like.dto;


import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LikeCountResponse {
    private Long boardId;
    private Long likeCount;
    private boolean isLiked;
}
