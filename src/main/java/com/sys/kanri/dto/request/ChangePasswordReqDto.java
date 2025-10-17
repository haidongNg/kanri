package com.sys.kanri.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChangePasswordReqDto {
    @NotBlank(message = "Mật khẩu cũ không được để trống.")
    private String oldPassword;
    @NotBlank(message = "Mật khẩu mới không được để trống.")
    @Size(min = 8, message = "Mật khẩu mới phải có ít nhất 8 ký tự.")
    private String newPassword;
}
