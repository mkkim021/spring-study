package com.kms.springboard.comment.service;

import com.kms.springboard.comment.dto.CommentCreateRequest;
import com.kms.springboard.comment.dto.CommentDto;
import com.kms.springboard.comment.entity.CommentEntity;
import com.kms.springboard.comment.repository.CommentRepository;
import com.kms.springboard.member.entity.MemberEntity;
import com.kms.springboard.member.service.MemberService;
import com.kms.springboard.post.entity.BoardEntity;
import com.kms.springboard.post.repository.BoardRepository;
import com.kms.springboard.post.service.BoardService;
import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
@Builder
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberService memberService;


    @Override
    public CommentDto createComment(CommentCreateRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null||!auth.isAuthenticated()) {
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
                .build();

        CommentEntity saved = commentRepository.save(buildComment);

        return convertToDto(saved);
    }

    private CommentDto convertToDto(CommentEntity entity) {

        return CommentDto.builder()
                .id(entity.getCommentId())
                .content(entity.getContent())
                .writer(entity.getWriter())
                .boardId(entity.getBoard().getId())
                .build();
    }

}
