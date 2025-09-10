package com.kms.springboard.member.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "password")
public class MemberDto {

    @NotBlank(message = "아이디를 입력해주세요")
    @Size(min=3,max=30,message = "아이디는 3~30자여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "아이디는 영문/숫자/._-만 허용합니다")
    private String userId;
    @NotBlank(message = "이름을 입력해주세요")
    private String username;
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    @Size(max = 255, message = "이메일은 255자 이하여야 합니다")
    private String email;
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min=3,max=64,message = "비밀번호는 3~64자여야 합니다")
    private String password;

}
