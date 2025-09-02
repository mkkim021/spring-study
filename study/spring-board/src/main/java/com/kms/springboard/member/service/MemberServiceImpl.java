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
        return memberRepository.save(member);

    }

    @Override
    public MemberEntity saveDto(MemberDto memberDto) {
        MemberEntity existing = memberRepository.findByUsername(memberDto.getUsername());
        if(existing != null) {
            throw new IllegalStateException("이미 존재하는 사용자입니다");
        }
        String encoded = passwordEncoder.encode(memberDto.getPassword());
        MemberEntity member = MemberEntity.builder()
                .username(memberDto.getUsername())
                .password(encoded)
                .build();
        return memberRepository.save(member);
    }

    @Override
    public boolean isLogin(LoginDto loginDto) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        MemberEntity byUsername = memberRepository.findByUsername(username);
        return byUsername != null && password.equals(byUsername.getPassword());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberEntity findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }
}
