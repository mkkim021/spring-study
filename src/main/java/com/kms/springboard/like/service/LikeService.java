package com.kms.springboard.like.service;

import com.kms.springboard.like.dto.LikeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikeService {
    boolean toggleLike(Long boardId, String userId);
    Long getLikeCount(Long boardId);
    Page<LikeDto> getLikeUsers(Long boardId, Pageable pageable);
    Page<LikeDto> getUserLikeBoards(String userId, Pageable pageable);
}
