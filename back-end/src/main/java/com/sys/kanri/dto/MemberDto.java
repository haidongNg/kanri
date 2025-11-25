package com.sys.kanri.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto extends BaseDto {
    @NotBlank(message = "Username không được để trống.")
    private String username;

    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Email phải đúng định dạng.")
    private String email;

    @NotBlank(message = "Họ và tên không được để trống.")
    private String fullName;;

    @NotBlank(message = "Số điện thoại không được để trống.")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Số điện thoại không hợp lệ.")
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống.")
    private String address;

    @NotBlank(message = "Giới tính không được để trống.")
    private String gender;

    private String imageUrl;
}
