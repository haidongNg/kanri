package com.sys.kanri.dto;

import com.sys.kanri.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDto {
    private String fullName;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String gender;
    private Role role;
}
