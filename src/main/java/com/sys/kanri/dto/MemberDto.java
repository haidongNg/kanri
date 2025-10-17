package com.sys.kanri.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @Digits(integer = 10, fraction = 0, message = "Số điện thoại phải là 10 chữ số.")
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống.")
    private String address;

    @NotBlank(message = "Giới tính không được để trống.")
    private String gender;

    private String imageUrl;
}
