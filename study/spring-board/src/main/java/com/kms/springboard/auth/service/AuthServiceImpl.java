package com.kms.springboard.auth.service;

import com.kms.springboard.auth.dto.JwtTokenResponse;
import com.kms.springboard.auth.dto.LoginDto;
import com.kms.springboard.auth.jwt.JwtTokenGenerator;
import com.kms.springboard.auth.jwt.JwtTokenProvider;
import com.kms.springboard.auth.jwt.JwtTokenValidator;
import com.kms.springboard.common.Normalizer;
import com.kms.springboard.member.entity.MemberEntity;
import com.kms.springboard.member.repository.MemberRepository;
import io.netty.util.internal.StringUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;
    private final TokenService tokenService;
    private final Normalizer normalizer;


    @Override
    public JwtTokenResponse login(LoginDto loginDto) {
        String normalizedUserId = normalizer.normalize(loginDto.getUserId());

        MemberEntity member = memberRepository.findByUserId(normalizedUserId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

        if(!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new BadCredentialsException(" 비밀번호가 일치하지 않습니다");
        }
        JwtTokenResponse tokenResponse = jwtTokenGenerator.generateToken(normalizedUserId);

        tokenService.saveRefreshToken(normalizedUserId, tokenResponse.getRefreshToken());

        log.info("사용자 로그인 성공: {}", normalizedUserId);
        return tokenResponse;
    }

    @Override
    public JwtTokenResponse refreshToken(String refreshToken) {
        if(!StringUtils.hasText(refreshToken)) {
            throw new BadCredentialsException("Refresh token이 필요합니다")
        }
        if(!jwtTokenValidator.validate(refreshToken)) {
            throw new BadCredentialsException("유효하지 않은 refresh token입니다");
        }

        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        if(!tokenService.validateRefreshToken(userId, refreshToken)) {
            throw new BadCredentialsException("저장된 refresh token과 일치하지 않습니다");
        }

        JwtTokenResponse newTokenResponse = jwtTokenGenerator.generateToken(userId);

        tokenService.deleteRefreshToken(userId);
        tokenService.saveRefreshToken(userId, newTokenResponse.getRefreshToken());

        log.info("토큰 리프레시 완료: {}", userId);

        return newTokenResponse;
    }


    @Override
    public void logout(String authHeader, String refreshToken, HttpServletResponse response) {
        String userId = null;

        if(StringUtils.hasText(authHeader) &&  authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);

            try{
                userId = jwtTokenProvider.getUserIdFromToken(accessToken);
                tokenService.addToBlacklist(accessToken);
                log.debug("Access token 블랙리스트 추가 완료");
            }catch(Exception e){
                log.warn("Access token 처리 중 오류 발생: {}", e.getMessage());
            }
        }

        if(StringUtils.hasText(refreshToken) && userId != null) {
            tokenService.deleteRefreshToken(userId);
            log.debug("Refresh token 삭제 완료");
        }

        clearRefreshTokenCookie(response);

        if(userId != null) {
            log.info("사용자 로그안웃 완료: {}", userId);
        }
    }

    @Override
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("REFRESH_TOKEN", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);
        log.debug("Refresh token 쿠키 설정 완료");
    }

    @Override
    public boolean validateToken(String authHeader) {
        if(!StringUtils.hasText(authHeader)) {
            return false;
        }

        String token = authHeader.substring(7);

        if(!jwtTokenValidator.validate(token)) {
            return false;

        }
        if (tokenService.isBlacklisted(token)) {
            log.debug("블랙리스트에 등록된 토큰입니다");
            return false;
        }
        return true;
    }


    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("REFRESH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
        log.debug("Refresh token 쿠키 삭제 완료");
    }
}
