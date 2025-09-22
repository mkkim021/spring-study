package com.kms.springboard.post.service;


import com.kms.springboard.post.dto.BoardDto;
import com.kms.springboard.post.entity.BoardEntity;
import com.kms.springboard.post.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;



@RequiredArgsConstructor
@Service
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public BoardDto save(BoardDto boardDto) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth==null||!auth.isAuthenticated()) {
            throw new AccessDeniedException("인증이 필요합니다");
        }
        BoardEntity buildEntity = BoardEntity.builder()
                .title(boardDto.getTitle())
                .writer(auth.getName())
                .content(boardDto.getContent())
                .postPassword(passwordEncoder.encode(boardDto.getPostPassword()))
                .build();
        BoardEntity save = boardRepository.save(buildEntity);
        return BoardDto.builder()
                .id(save.getId())
                .title(save.getTitle())
                .content(save.getContent())
                .writer(save.getWriter())
                .build();

    }



    @Override
    @Transactional(readOnly = true)
    public Page<BoardDto> findAll(Pageable pageable) {
        Page<BoardEntity> page = boardRepository.findAll(pageable);
        return page.map(this::convertToDto);
    }

    @Override
    public BoardDto findById(Long id) {
        BoardEntity entity = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + id));
        return convertToDto(entity);
    }


    // Entity -> DTO 변환 메서드
    private BoardDto convertToDto(BoardEntity entity) {
        return BoardDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getWriter())
                .build();
    }






    @Override
    public void delete(Long id) {
        BoardEntity boardEntity = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found:" + id));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth==null||!auth.isAuthenticated()) {
            throw new AccessDeniedException("인증이 필요합니다");
        }
        String currentUser = auth.getName();
        if(!Objects.equals(boardEntity.getWriter(),currentUser)){
            throw new AccessDeniedException("작성자만 게시글을 삭제할 수 있습니다");
        }
        boardRepository.delete(boardEntity);

    }



    @Override
    public void updateWithPassword(Long boardId, BoardDto updateBoardDto, String rawPostPassword) throws AccessDeniedException {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found:" + boardId));
        var auth = SecurityContextHolder.getContext().getAuthentication();
        final String currentUser = auth == null ? null:auth.getName();
        if(!Objects.equals(boardEntity.getWriter(),currentUser)) {
            throw new AccessDeniedException("해당 게시물 작성자가 아닙니다");
        }

        boolean postPasswordMatches = passwordEncoder.matches(rawPostPassword, boardEntity.getPostPassword());
        if(!postPasswordMatches) {
            throw new AccessDeniedException("비밀번호가 일치하지 않습니다");

        }
        boardEntity.update(updateBoardDto.getTitle(), updateBoardDto.getContent());

    }



}
