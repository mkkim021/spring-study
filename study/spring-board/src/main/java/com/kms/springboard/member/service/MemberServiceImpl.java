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
        MemberEntity existing = memberRepository.findByUserId(memberDto.getUserId());
        if(existing != null) {
            throw new IllegalStateException("이미 존재하는 아이디입니다");
        }
        //이메일 중복 체크
        MemberEntity existingByEmail = memberRepository.findByEmail(memberDto.getEmail());
        if(existingByEmail != null) {
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
        if(userId == null || userId.isBlank()){
            return false;
        }

        MemberEntity findUserId = memberRepository.findByUserId(userId);

        if(password == null || password.isBlank()) {
            return false;
        }
        if(findUserId == null) {
            return false;
        }
        return passwordEncoder.matches(password, findUserId.getPassword());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberEntity findByUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }
}
