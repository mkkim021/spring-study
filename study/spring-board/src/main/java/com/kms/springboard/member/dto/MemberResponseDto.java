package com.kms.springboard.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class MemberResponseDto {
    private String userId;
    private String username;
    private String email;
}
