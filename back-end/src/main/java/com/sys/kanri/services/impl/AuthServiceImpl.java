package com.sys.kanri.services.impl;

import com.sys.kanri.dto.response.AuthResDto;
import com.sys.kanri.entities.Member;

import com.sys.kanri.exceptions.ApiException;
import com.sys.kanri.security.JwtService;
import com.sys.kanri.services.AuthService;
import com.sys.kanri.services.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.sys.kanri.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;
    private final JwtService jwtService;

    /**
     * Authenticates a user using the provided username and password,
     * generates a JWT if authentication is successful, and stores the JWT
     * in an HttpOnly cookie in the response.
     *
     * <p>Steps performed:
     * <ul>
     *   <li>Authenticate credentials using Spring {@link AuthenticationManager}.</li>
     *   <li>Load user details from {@link MemberService}.</li>
     *   <li>Generate JWT using {@link JwtService}.</li>
     *   <li>Create an HttpOnly cookie containing the JWT and attach it to the response.</li>
     * </ul>
     *
     * @param username the username provided by the client
     * @param password the raw password provided by the client
     * @throws RuntimeException if authentication fails
     */
    @Override
    public AuthResDto authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (AuthenticationException e) {
            throw new ApiException(INVALID_CREDENTIALS.message, INVALID_CREDENTIALS.code, INVALID_CREDENTIALS.status);
        }

        Member memberDetail = (Member) memberService.loadUserByUsername(username);
        String accessToken = jwtService.generateAccessToken(Map.of("role", memberDetail.getRole().getName()), memberDetail.getUsername());
        String refreshToken = jwtService.generateRefreshToken(memberDetail.getUsername());

        AuthResDto result = new AuthResDto();
        result.setAccessToken(accessToken);
        result.setRefreshToken(refreshToken);
        return result;
    }

    @Override
    public AuthResDto refreshToken(String refreshToken) {
        // 1. Kiểm tra token có null không
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new ApiException("Refresh Token không tồn tại", "AUTH_401", HttpStatus.UNAUTHORIZED);
        }

        // 2. Lấy username từ token
        String username = jwtService.extractUsername(refreshToken);
        Member member = (Member) memberService.loadUserByUsername(username);

        // 3. Validate token
        if (!jwtService.isTokenValid(refreshToken, member.getUsername())) {
            throw new ApiException("Refresh Token hết hạn hoặc không hợp lệ", "AUTH_403", HttpStatus.FORBIDDEN);
        }

        // 4. Tạo Access Token mới
        String newAccessToken = jwtService.generateAccessToken(
                Map.of("role", member.getRole().getName()),
                member.getUsername()
        );

        // 5. Trả về
        AuthResDto result = new AuthResDto();
        result.setAccessToken(newAccessToken);
        result.setRefreshToken(refreshToken); // Giữ nguyên refresh token cũ (hoặc tạo mới nếu muốn xoay vòng)
        return result;
    }
}