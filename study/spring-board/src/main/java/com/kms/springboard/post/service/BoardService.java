package com.kms.springboard.post.service;


import com.kms.springboard.post.dto.BoardDto;
import com.kms.springboard.post.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

public interface BoardService {
    BoardDto save(BoardDto boardDto);
    Page<BoardDto> findAll(Pageable pageable);
    BoardDto findById(Long id);
    void delete(Long id);

    void updateWithPassword(Long boardId, BoardDto updateBoardDto, String rawPassword) throws AccessDeniedException;
}
