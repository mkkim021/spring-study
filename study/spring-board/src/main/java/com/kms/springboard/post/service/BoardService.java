package com.kms.springboard.post.service;


import com.kms.springboard.post.dto.BoardDto;
import com.kms.springboard.post.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
public interface BoardService {
    BoardDto save(BoardDto boardDto);

    List<BoardEntity> findByAll();

    Page<BoardDto> findAll(Pageable pageable);

    BoardDto findById(Long id);
    BoardDto getBoard(Long boardId);

    void delete(Long id, String writer);
    void updateWithPassword(Long boardId, BoardDto updateBoardDto, String rawPassword, String username)throws AccessDeniedException;
}
