package com.kms.springboard.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "password")
//@Data는 toString에 패스워드를 노출한다
public class LoginDto {
    @NotBlank(message = "아이디를 입력해주세요")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}
