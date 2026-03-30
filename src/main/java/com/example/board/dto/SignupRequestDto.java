package com.example.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @NotBlank
    @Size(min = 4, max = 10, message = "아이디는 4자 이상 10자 이하여야 합니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "아이디는 알파벳 소문자와 숫자로만 구성되어야 합니다.")
    private String username;

    @NotBlank
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "비밀번호는 알파벳 대소문자와 숫자로만 구성되어야 합니다.")
    private String password;

    // 관리자 권한을 부여받기 위한 선택 값
    private boolean admin = false;
    private String adminToken = "";
}
