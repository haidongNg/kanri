package com.sys.kanri.services.impl;

import com.sys.kanri.dto.AuthResponseDto;
import com.sys.kanri.dto.LoginRequestDto;
import com.sys.kanri.dto.RegisterRequestDto;
import com.sys.kanri.entities.MUser;

import com.sys.kanri.exceptions.ApiException;
import com.sys.kanri.services.IAuthService;
import com.sys.kanri.services.IJwtService;
import com.sys.kanri.services.IMUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static com.sys.kanri.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IMUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final IJwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user and returns a JWT token.
     */
    @Override
    public AuthResponseDto register(RegisterRequestDto request) {
        // Kiểm tra trùng username hoặc email
        if (userService.existsByUsername(request.getUsername())) {
            throw new ApiException(USERNAME_EXISTS.message, USERNAME_EXISTS.code, USERNAME_EXISTS.status);
        }
        if (userService.existsByEmail(request.getEmail())) {
            throw new ApiException(EMAIL_EXISTS.message, EMAIL_EXISTS.code, EMAIL_EXISTS.status);
        }

        MUser user = MUser.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .gender(request.getGender())
                .role(request.getRole())
                .build();

        userService.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponseDto(token);
    }

    /**
     * Authenticates the user and returns a JWT token.
     */
    @Override
    public AuthResponseDto authenticate(LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new ApiException(INVALID_CREDENTIALS.message,INVALID_CREDENTIALS.code,INVALID_CREDENTIALS.status);
        }

        MUser user = (MUser) userService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(user);
        return new AuthResponseDto(token);
    }
}