package com.kms.springboard.comment.service;

import com.kms.springboard.comment.dto.CommentDto;
import com.kms.springboard.comment.entity.CommentEntity;
import com.kms.springboard.comment.repository.CommentRepository;
import com.kms.springboard.member.entity.MemberEntity;
import com.kms.springboard.member.service.MemberService;
import com.kms.springboard.post.entity.BoardEntity;
import com.kms.springboard.post.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberService memberService;


    @Override
    public CommentDto createComment(CommentDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null||!auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("인증이 필요합니다");
        }
        BoardEntity board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다"));
        MemberEntity member = memberService.findByUserId(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));
        CommentEntity buildComment = CommentEntity.builder()
                .content(request.getContent())
                .writer(auth.getName())
                .board(board)
                .member(member)
                .userId(auth.getName())
                .build();

        CommentEntity saved = commentRepository.save(buildComment);

        return convertToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDto> findByBoardId(Long boardId, Pageable pageable) {
        Page<CommentEntity> findComment = commentRepository.findCommentsByBoardId(boardId,pageable);
        return findComment.map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDto> findByUserId(String userId, Pageable pageable) {
        Page<CommentEntity> commentsByMemberId = commentRepository.findCommentsByUserId(userId, pageable);
        return commentsByMemberId.map(this::convertToDto);
    }

    @Override
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 없습니다."));
        String writer = commentEntity.getWriter();
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null||!auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("인증이 필요합니다");
        }
        if(!auth.getName().equals(writer)) {
            throw new AccessDeniedException("해당 작성자만 수정할 수 있습니다");
        }

        commentEntity.update(commentDto.getContent());
        return convertToDto(commentEntity);
    }


    private CommentDto convertToDto(CommentEntity entity) {

        return CommentDto.builder()
                .id(entity.getCommentId())
                .content(entity.getContent())
                .writer(entity.getWriter())
                .boardId(entity.getBoard().getId())
                .memberId(entity.getMember().getId())
                .userId(entity.getUserId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }


}
