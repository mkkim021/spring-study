package com.kms.springboard.post.service;



import com.kms.springboard.member.entity.MemberEntity;
import com.kms.springboard.member.repository.MemberRepository;
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


import java.security.InvalidParameterException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@RequiredArgsConstructor
@Service
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public BoardDto save(BoardDto boardDto) {

        BoardEntity buildEntity = BoardEntity.builder()
                .title(boardDto.getTitle())
                .writer(SecurityContextHolder.getContext().getAuthentication()!=null?SecurityContextHolder.getContext().getAuthentication().getName(): boardDto.getWriter())
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
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다" + id));
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

    // DTO -> Entity 변환 메서드 (기존 save에서 사용)
    private BoardEntity convertToEntity(BoardDto dto) {
        return BoardEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .postPassword(passwordEncoder.encode(dto.getPostPassword()))
                .build();
    }




    @Override
    public void delete(Long id) {
        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Board not found:" + id));
        String currentUser = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .map(Authentication::getName).orElse(null);
        if(!Objects.equals(boardEntity.getWriter(), currentUser)) {
            throw new AccessDeniedException("작성자만 게시글을 삭제할 수 있습니다");
        }
        boardRepository.delete(boardEntity);

    }



    @Override
    public void updateWithPassword(Long boardId, BoardDto updateBoardDto, String rawPostPassword, String username) throws AccessDeniedException {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found:" + boardId));
        var auth = SecurityContextHolder.getContext().getAuthentication();
        final String currentUser = auth == null?null:auth.getName();
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
