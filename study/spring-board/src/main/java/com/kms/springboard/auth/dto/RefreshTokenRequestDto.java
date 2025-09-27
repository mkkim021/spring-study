package com.kms.springboard.auth.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RefreshTokenRequestDto {
    private String refreshToken;
}
