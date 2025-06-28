package com.sys.kanri.services;

import com.sys.kanri.dto.AuthResponseDto;
import com.sys.kanri.dto.LoginRequestDto;
import com.sys.kanri.dto.RegisterRequestDto;

/**
 * Interface for authentication and registration service.
 */
public interface IAuthService {
    /**
     * Registers a new user and returns an authentication token.
     *
     * @param request the registration data
     * @return JWT authentication response
     */
    public AuthResponseDto register(RegisterRequestDto request);

    /**
     * Authenticates the user and returns a JWT token.
     *
     * @param request login credentials
     * @return JWT authentication response
     */
    AuthResponseDto authenticate(LoginRequestDto request);
}
