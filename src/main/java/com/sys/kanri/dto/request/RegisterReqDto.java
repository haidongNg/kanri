package com.sys.kanri.dto.request;

import com.sys.kanri.dto.MemberDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RegisterReqDto extends MemberDto {
    @NotBlank
    private String password;
}
