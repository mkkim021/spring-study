package com.kms.springboard.member.service;

import com.kms.springboard.common.Normalizer;
import com.kms.springboard.member.dto.MemberDto;
import com.kms.springboard.member.entity.MemberEntity;
import com.kms.springboard.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final Normalizer normalizer;

    @Override
    public MemberEntity saveDto(MemberDto memberDto) {
        final var normalizedUserId = normalizer.normalize(memberDto.getUserId());
        final var normalizedEmail =  normalizer.normalize(memberDto.getEmail());
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
    public Optional<MemberEntity> findByUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }



}
