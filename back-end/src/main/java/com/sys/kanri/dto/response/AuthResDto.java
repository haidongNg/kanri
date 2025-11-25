package com.sys.kanri.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResDto {
    private String accessToken;
    private String refreshToken;
}
