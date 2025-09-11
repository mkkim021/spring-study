package com.kms.springboard.member.service;


import com.kms.springboard.member.dto.LoginDto;
import com.kms.springboard.member.dto.MemberDto;
import com.kms.springboard.member.entity.MemberEntity;
import lombok.RequiredArgsConstructor;

import java.util.Optional;


public interface MemberService {

    MemberEntity save(MemberEntity member);
    MemberEntity saveDto(MemberDto memberDto);
    boolean isLogin(LoginDto loginDto);
    Optional <MemberEntity> findByUserId(String userId);
    String login(LoginDto loginDto);
}
