package com.kms.springboard.post.service;


import com.kms.springboard.post.dto.BoardDto;
import com.kms.springboard.post.entity.BoardEntity;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface BoardService {
    BoardEntity save(BoardDto boardDto);

    List<BoardEntity> findByAll();

    BoardDto getBoard(Long boardId);

    void delete(Long id, String writer);
    void update(Long id,BoardDto updateBoardDto);
    boolean verifyPassword(Long boardId, String rawPassword, String username);
    void updateWithPassword(Long boardId, BoardDto updateBoardDto, String rawPassword, String username);
}
