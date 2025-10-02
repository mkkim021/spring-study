package com.kms.springboard.like.repository;


import com.kms.springboard.like.entity.LikeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    boolean existsByBoardIdAndUserId(Long boardId, String userId);

    @Query("SELECT l FROM LikeEntity l WHERE l.board.id = :boardId ORDER BY l.createdAt DESC")
    Page<LikeEntity> findByBoardId(Long boardId, Pageable pageable);

    @Query("SELECT l FROM LikeEntity l WHERE l.userId = :userId ORDER BY l.createdAt DESC")
    Page<LikeEntity> findByUserId(Long userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM LikeEntity l WHERE l.board.id = :boarId AND l.userId = :userId")
    void deleteByBoardIDAndUserId(Long boardId, String userId);

    @Query("SELECT COUNT(l) FROM LikeEntity l WHERE l.board.id = :boardId")
    long countByBoardId(Long boardId);
}
