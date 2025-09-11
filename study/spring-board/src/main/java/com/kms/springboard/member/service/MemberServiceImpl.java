package com.kms.springboard.member.service;

import com.kms.springboard.config.JwtTokenProvider;
import com.kms.springboard.member.dto.LoginDto;
import com.kms.springboard.member.dto.MemberDto;
import com.kms.springboard.member.entity.MemberEntity;
import com.kms.springboard.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public MemberEntity save(MemberEntity member) {
        String pw = member.getPassword();
        if(pw == null || !pw.startsWith("$2")){
            throw new IllegalArgumentException("Password must be encoded before save()");
        }
        return memberRepository.save(member);

    }

    @Override
    public MemberEntity saveDto(MemberDto memberDto) {
        final var normalizedUserId = memberDto.getUserId().trim().toLowerCase(Locale.ROOT);
        final var normalizedEmail = memberDto.getEmail().trim().toLowerCase(Locale.ROOT);
        if(memberRepository.existsByUserId(normalizedUserId)) {
            throw new IllegalStateException("이미 존재하는 아이디입니다");
        }
        //이메일 중복 체크
        if(memberRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalStateException("이미 존재하는 이메일입니다");
        }
        String encoded = passwordEncoder.encode(memberDto.getPassword());
        MemberEntity member = MemberEntity.builder()
                .userId(normalizedUserId)
                .username(memberDto.getUsername())
                .email(normalizedEmail)
                .password(encoded)
                .build();
        return memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLogin(LoginDto loginDto) {
        String userId = loginDto.getUserId();
        String password = loginDto.getPassword();

        if(userId == null || userId.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        final String normalized = userId == null ? "" : userId.trim().toLowerCase(Locale.ROOT);
        return memberRepository.findByUserId(normalized)
                .map(member-> passwordEncoder.matches(password,member.getPassword()))
                        .orElse(false);



    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberEntity> findByUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }


    @Override
    @Transactional(readOnly = true)
    public String login(LoginDto loginDto) {
        if(!isLogin(loginDto)){
            throw new EntityNotFoundException("아이디 또는 비밀번호가 잘못되었습니다");
        }
        final String normalizedUserId = loginDto.getUserId().trim().toLowerCase(Locale.ROOT);
        return jwtTokenProvider.generateToken(normalizedUserId);
    }

}
