package com.kms.springboard.like.service;

import com.kms.springboard.like.dto.LikeDto;
import com.kms.springboard.like.entity.LikeEntity;
import com.kms.springboard.like.repository.LikeRepository;
import com.kms.springboard.member.entity.MemberEntity;
import com.kms.springboard.member.repository.MemberRepository;
import com.kms.springboard.post.entity.BoardEntity;
import com.kms.springboard.post.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String LIKE_COUNT_PREFIX = "board:like:count:";
    private static final String USER_LIKED_PREFIX = "board:like:user:";
    private static final Duration CACHE_TTL = Duration.ofHours(24);

    @Override
    public boolean toggleLike(Long boardId, String userId) {

        boolean exists = likeRepository.existsByBoardIdAndUserId(boardId, userId);

        if (exists) {
            removeLike(boardId, userId);
            return false;
        } else {
            addLike(boardId, userId);
            return true;
        }
    }

    @Override
    public Long getLikeCount(Long boardId) {
        String cacheKey = LIKE_COUNT_PREFIX + boardId;
        String cached = redisTemplate.opsForValue().get(cacheKey);

        if(cached != null) {
            return Long.parseLong(cached);
        }

        long count = likeRepository.countByBoardId(boardId);
        redisTemplate.opsForValue().set(cacheKey, String.valueOf(count), CACHE_TTL);

        return count;
    }

    @Override
    public boolean isLikeByUserId(Long boardId, String userId) {
        String cacheKey = USER_LIKED_PREFIX + boardId;
        String cached = redisTemplate.opsForValue().get(cacheKey);

        if(cached != null) {
            return Boolean.parseBoolean(cached);
        }
        boolean isLiked = likeRepository.existsByBoardIdAndUserId(boardId, userId);
        cacheUserLiked(boardId,userId,isLiked);

        return isLiked;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LikeDto> getLikeUsers(Long boardId, Pageable pageable) {
        Page<LikeEntity> likes = likeRepository.findByBoardId(boardId,pageable);
        return likes.map(LikeDto::convertToDto);
    }

    @Override
    public Page<LikeDto> getUserLikeBoards(String userId, Pageable pageable) {
        return null;
    }
    private void addLike(Long boardId, String userId) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다"));

        MemberEntity member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

        LikeEntity like = LikeEntity.builder()
                .board(board)
                .member(member)
                .userId(userId)
                .build();

        likeRepository.save(like);

        incrementLikeCount(boardId);
        cacheUserLiked(boardId,userId,true);

    }

    private void removeLike(Long boardId, String userId) {
        likeRepository.deleteByBoardIDAndUserId(boardId, userId);
        log.debug("like removed: boardId={}, userId={}", boardId, userId);

        decrementLikeCount(boardId);
        cacheUserLiked(boardId, userId, false);
    }

    private void incrementLikeCount(Long boardId){
        String cacheKey = LIKE_COUNT_PREFIX + boardId;
        redisTemplate.opsForValue().increment(cacheKey);
        redisTemplate.expire(cacheKey,CACHE_TTL);
        log.debug("like count incremented in Redis: boardId={}", boardId);

    }

    private void decrementLikeCount(Long boardId){
        String cacheKey = LIKE_COUNT_PREFIX + boardId;
        Long count = redisTemplate.opsForValue().decrement(cacheKey);
        if(count != null && count < 0){
            redisTemplate.opsForValue().set(cacheKey, "0", CACHE_TTL);
        }
        log.debug("like count decremented in Redis: boardId={}", boardId);
    }

    private void cacheUserLiked(Long boardId, String userId, boolean isLiked) {
        String cacheKey = USER_LIKED_PREFIX + boardId + ":" + userId;
        redisTemplate.opsForValue().set(cacheKey, String.valueOf(isLiked), CACHE_TTL);
    }

}





