package com.kms.springboard.member.service;


import com.kms.springboard.member.dto.MemberDto;
import com.kms.springboard.member.entity.MemberEntity;

import java.util.Optional;


public interface MemberService {

    MemberEntity saveDto(MemberDto memberDto);
    Optional <MemberEntity> findByUserId(String userId);
}
