package com.sys.kanri.services;

import com.sys.kanri.dto.response.AuthResDto;

public interface AuthService {
    AuthResDto authenticate(String username, String password);
    AuthResDto refreshToken(String refreshToken);
}
