package com.sys.kanri.dto.response;

import com.sys.kanri.dto.MemberDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResDto extends MemberDto {
    private String role;
}
