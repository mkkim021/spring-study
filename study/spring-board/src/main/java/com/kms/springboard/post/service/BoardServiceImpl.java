package com.kms.springboard.post.service;


import com.kms.springboard.post.dto.BoardDto;
import com.kms.springboard.post.entity.BoardEntity;
import com.kms.springboard.post.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.channels.IllegalChannelGroupException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    @Override
    public BoardEntity save(BoardDto boardDto) {
        BoardEntity buildEntity = BoardEntity.builder()
                .title(boardDto.getTitle())
                .writer(boardDto.getWriter())
                .content(boardDto.getContent())
                .build();
        BoardEntity save = boardRepository.save(buildEntity);
        return save;
    }



    @Override
    public List<BoardEntity> findByAll() {
        return boardRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public BoardDto getBoard(Long boardId) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found:" + boardId));

        BoardDto boardDto = BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .build();
        return boardDto;
    }


    @Override
    public void delete(Long id) {
        boardRepository.deleteById(id);

    }

    @Override
    public void update(Long id, BoardDto updateBoardDto) {
        BoardEntity board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found:" + id));
        board.update(updateBoardDto.getTitle(), updateBoardDto.getContent());
        // 이러면 업데이트된 정보를 Transactional 범위 내에서 더티체킹으로 자동 반영
        // 이중 save할 필요없음
    }
}
