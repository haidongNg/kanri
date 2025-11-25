package com.sys.kanri.services;

import com.sys.kanri.dto.response.AuthResDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResDto authenticate(String username, String password, HttpServletResponse response);
}
