package com.kms.springboard.post.service;



import com.kms.springboard.member.repository.MemberRepository;
import com.kms.springboard.post.dto.BoardDto;
import com.kms.springboard.post.entity.BoardEntity;
import com.kms.springboard.post.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.security.InvalidParameterException;
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

        if(boardDto.getPassword() == null || boardDto.getPassword().isEmpty()) {
            throw new InvalidParameterException("비밀번호는 필수입니다");
        }
        BoardEntity buildEntity = BoardEntity.builder()
                .title(boardDto.getTitle())
                .writer(boardDto.getWriter())
                .content(boardDto.getContent())
                .password(passwordEncoder.encode(boardDto.getPassword()))
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

    @Override
    public boolean verifyPassword(Long boardId, String rawPassword, String username) {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found:" + boardId));
        
        // 작성자 검증
        if(!boardEntity.getWriter().equals(username)) {
            throw new InvalidParameterException("해당 게시글의 작성자가 아닙니다");
        }
        //비밀번호 검증
       return passwordEncoder.matches(rawPassword, boardEntity.getPassword());
    }

    @Override
    public void updatePassword(Long boardId, BoardDto updateBoardDto, String rawPassword, String username) {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found:" + boardId));
        if (!boardEntity.getWriter().equals(username)) {
            throw new InvalidParameterException("해당 게시물 작성자가 아닙니다");
        }
        boolean passwordMatches;
        if(isPasswordHashed(boardEntity.getPassword())) {
            passwordMatches = passwordEncoder.matches(rawPassword,boardEntity.getPassword());

        }
        else{
            passwordMatches = boardEntity.getPassword().equals(rawPassword);
        }
        if(!passwordMatches) {
            throw new InvalidParameterException("비밀번호가 일치하지 않습니다");
        }
        boardEntity.update(updateBoardDto.getTitle(), updateBoardDto.getContent());

        if(updateBoardDto.getPassword() != null && !updateBoardDto.getPassword().isEmpty()) {
            boardEntity.updatePassword(passwordEncoder.encode(updateBoardDto.getPassword()));


        }
    }


    private boolean isPasswordHashed(String password) {
        return password != null && password.startsWith("$2");
    }

}
