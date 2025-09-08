package com.kms.springboard.post.service;


import com.kms.springboard.post.dto.BoardDto;
import com.kms.springboard.post.entity.BoardEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
public interface BoardService {
    BoardEntity save(BoardDto boardDto);

    List<BoardEntity> findByAll();

    BoardDto getBoard(Long boardId);

    void delete(Long id, String writer);
    void updateWithPassword(Long boardId, BoardDto updateBoardDto, String rawPassword, String username)throws AccessDeniedException;
}
