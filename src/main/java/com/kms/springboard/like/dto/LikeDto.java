package com.kms.springboard.like.dto;


import com.kms.springboard.like.entity.LikeEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {

    private Long id;
    private String userId;
    private Long boardId;
    private LocalDateTime createdAt;

    public static LikeDto convertToDto(LikeEntity entity) {
        return LikeDto.builder()
                .id(entity.getId())
                .boardId(entity.getBoard().getId())
                .userId(entity.getUserId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
