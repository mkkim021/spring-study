package com.kms.springboard.post.service;


import com.kms.springboard.post.dto.BoardDto;
import com.kms.springboard.post.entity.BoardEntity;
import com.kms.springboard.post.repository.BoardRepository;
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
                .id(boardDto.getId())
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
    public BoardDto getBoard(Long boardId) {
        Optional<BoardEntity> boardEntityWrapper = boardRepository.findById(boardId);
        BoardEntity board = boardEntityWrapper.get();

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
        BoardDto board = getBoard(id);
        board.setTitle(updateBoardDto.getTitle());
        board.setContent(updateBoardDto.getContent());
        BoardEntity updateBoard = save(board);
        boardRepository.save(updateBoard);
    }
}
