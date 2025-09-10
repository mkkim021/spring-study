package com.kms.springboard.member.controller;

import com.kms.springboard.common.dto.ApiResponse;
import com.kms.springboard.member.dto.LoginDto;
import com.kms.springboard.member.dto.MemberDto;
import com.kms.springboard.member.dto.MemberResponseDto;
import com.kms.springboard.member.entity.MemberEntity;
import com.kms.springboard.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody MemberDto memberDto){
        try{
            memberService.saveDto(memberDto);
            return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다",null));

        }catch (Exception e){
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("회원가입 실패" + e.getMessage()));

        }
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            String token = memberService.login(loginDto);
            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .userId(loginDto.getUserId())
                    .build();
            return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인 실패:"));
        }


    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<MemberDto>> getMember(@PathVariable String userId) {
        memberService.findByUserId(userId)
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

        return ResponseEntity.notFound().build();
    }

}
@Data
@Builder
class LoginResponse{
    private String token;
    private String userId;
}

