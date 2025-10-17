package com.sys.kanri.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginReqDto {
    @NotBlank(message = "Username không được để trống.")
    private String username;

    @NotBlank(message = "Password không được để trống.")
    private String password;
}
