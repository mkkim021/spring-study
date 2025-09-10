package com.kms.springboard.member.controller;

import com.kms.springboard.common.dto.ApiResponse;
import com.kms.springboard.member.dto.LoginDto;
import com.kms.springboard.member.dto.LoginResponse;
import com.kms.springboard.member.dto.MemberDto;
import com.kms.springboard.member.dto.MemberResponseDto;
import com.kms.springboard.member.entity.MemberEntity;
import com.kms.springboard.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Optional;

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
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            String token = memberService.login(loginDto);
            var jwtCookie = ResponseCookie.from("ACCESS_TOKEN", token)
                    .httpOnly(true).secure(true).sameSite("Strict").path("/")
                    .maxAge(3600)
                    .build();
            LoginResponse response = LoginResponse.builder()
                    .userId(loginDto.getUserId())
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(ApiResponse.success("로그인 성공", response));
        } catch (Exception e) {
            log.warn("로그인 실패", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인 실패:"));
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

