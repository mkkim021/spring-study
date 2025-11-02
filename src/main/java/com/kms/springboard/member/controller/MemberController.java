package com.kms.springboard.member.controller;

import com.kms.springboard.common.dto.ApiResponse;
import com.kms.springboard.member.dto.MemberDto;
import com.kms.springboard.member.dto.MemberResponseDto;
import com.kms.springboard.member.service.MemberService;
import com.kms.springboard.auth.jwt.JwtTokenGenerator;
import com.kms.springboard.auth.jwt.JwtTokenProvider;
import com.kms.springboard.auth.jwt.JwtTokenValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;


@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/users")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody MemberDto memberDto){
        try{
            memberService.saveDto(memberDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("회원가입이 완료되었습니다",null));

        }catch (Exception e){
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("회원가입 실패"));

        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<MemberResponseDto>> getMember(@PathVariable String userId) {
        final String normalizedUserId = userId.trim().toLowerCase(Locale.ROOT);
        return memberService.findByUserId(normalizedUserId)
                .map(member -> {
                    MemberResponseDto res = MemberResponseDto.builder()
                            .userId(member.getUserId())
                            .username(member.getUsername())
                            .email(member.getEmail())
                            .build();
                    return ResponseEntity.ok(ApiResponse.success("회원 조회 성공", res));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("회원을 찾을 수 없습니다")));

    }

}

