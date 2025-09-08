package com.kms.springboard.post.service;



import com.kms.springboard.member.repository.MemberRepository;
import com.kms.springboard.post.dto.BoardDto;
import com.kms.springboard.post.entity.BoardEntity;
import com.kms.springboard.post.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.security.InvalidParameterException;
import java.security.Principal;
import java.util.List;


@RequiredArgsConstructor
@Service
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public BoardEntity save(BoardDto boardDto) {

        BoardEntity buildEntity = BoardEntity.builder()
                .title(boardDto.getTitle())
                .writer(boardDto.getWriter())
                .content(boardDto.getContent())
                .postPassword(passwordEncoder.encode(boardDto.getPostPassword()))
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
    public void delete(Long id, String writer) {
        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Board not found:" + id));
        if(!boardEntity.getWriter().equals(writer)) {
            throw new AccessDeniedException("작성자만 게시글을 삭제할 수 있습니다");
        }
        boardRepository.delete(boardEntity);


    }



    @Override
    public void updateWithPassword(Long boardId, BoardDto updateBoardDto, String rawPostPassword, String username) throws AccessDeniedException {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found:" + boardId));
        if (!boardEntity.getWriter().equals(username)) {
            throw new AccessDeniedException("해당 게시물 작성자가 아닙니다");
        }
        boolean postPasswordMatches = passwordEncoder.matches(rawPostPassword, boardEntity.getPostPassword());
        if(!postPasswordMatches) {
            throw new AccessDeniedException("비밀번호가 일치하지 않습니다");

        }
        boardEntity.update(updateBoardDto.getTitle(), updateBoardDto.getContent());

        if(updateBoardDto.getPostPassword() != null && !updateBoardDto.getPostPassword().isEmpty()) {
            boardEntity.updatePassword(passwordEncoder.encode(updateBoardDto.getPostPassword()));
        }
    }


    private boolean isPasswordHashed(String password) {
        return password != null && password.startsWith("$2");
    }

}
