package com.kms.springboard.member.controller;

import com.kms.springboard.common.dto.ApiResponse;
import com.kms.springboard.member.dto.LoginDto;
import com.kms.springboard.member.dto.LoginResponse;
import com.kms.springboard.member.dto.MemberDto;
import com.kms.springboard.member.dto.MemberResponseDto;
import com.kms.springboard.member.entity.MemberEntity;
import com.kms.springboard.member.service.MemberService;
import com.kms.springboard.security.jwt.JwtTokenGenerator;
import com.kms.springboard.security.jwt.JwtTokenProvider;
import com.kms.springboard.security.jwt.JwtTokenValidator;
import com.kms.springboard.security.jwt.dto.JwtTokenResponse;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/users")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenGenerator jwtTokenGenerator;

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
    public ResponseEntity<ApiResponse<JwtTokenResponse>> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            JwtTokenResponse tokenResponse = memberService.login(loginDto);

            var accessTokenCookie = ResponseCookie.from("ACCESS_TOKEN", tokenResponse.getAccessToken())
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(3600)
                    .build();

            var refreshTokenCookie = ResponseCookie.from("REFRESH_TOKEN", tokenResponse.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(3600 * 24 * 7)
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(ApiResponse.success("로그인 성공", tokenResponse));
        } catch (EntityNotFoundException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("로그인 실패:"));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("로그인 처리 중 오류 발생했습니다"));
        }


    }
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtTokenResponse>>refresh(
            @CookieValue(value = "REFRESH_TOKEN",required = false)String refreshToken,
            @RequestBody(required = false)Map<String,String> requestBody){
        try{
            String token = refreshToken;
            if(token == null && requestBody != null){
                token = requestBody.get("refresh_token");
            }
            if(token == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Refresh token이 필요합니다"));
            }

            if(!jwtTokenValidator.validate(token)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("유효하지 않은 refresh token입니다"));
            }

            String userId = jwtTokenProvider.getUserIdFromToken(token);
            if(userId.startsWith("refresh:")){
                userId = userId.substring(8);
            }

            JwtTokenResponse newToken = jwtTokenGenerator.generateToken(userId);

            var accessTokenCookie = ResponseCookie.from("ACCESS_TOKEN", newToken.getAccessToken())
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(3600)
                    .build();

            var newRefreshTokenCookie = ResponseCookie.from("REFRESH_TOKEN", newToken.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(3600 * 24 * 7)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE,accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE,newRefreshTokenCookie.toString())
                    .build();
        }catch (Exception e){
            log.error("토큰 갱신 중 오류 발생",e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("토큰 갱신에 실패했습니다"));
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

