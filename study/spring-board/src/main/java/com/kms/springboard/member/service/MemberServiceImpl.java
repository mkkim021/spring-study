package com.kms.springboard.member.service;

import com.kms.springboard.member.dto.LoginDto;
import com.kms.springboard.member.dto.MemberDto;
import com.kms.springboard.member.entity.MemberEntity;
import com.kms.springboard.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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
        if(memberRepository.existsByUserId(memberDto.getUserId())) {
            throw new IllegalStateException("이미 존재하는 아이디입니다");
        }
        //이메일 중복 체크
        if(memberRepository.existsByEmail(memberDto.getEmail())) {
            throw new IllegalStateException("이미 존재하는 이메일입니다");
        }
        String encoded = passwordEncoder.encode(memberDto.getPassword());
        MemberEntity member = MemberEntity.builder()
                .userId(memberDto.getUserId())
                .username(memberDto.getUsername())
                .email(memberDto.getEmail())
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


        return memberRepository.findByUserId(userId).map(member -> passwordEncoder.matches(password, member.getPassword()))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberEntity> findByUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }
}
