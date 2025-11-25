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
     * @param response the HTTP response where the JWT cookie will be set
     * @throws RuntimeException if authentication fails
     */
    @Override
    public AuthResDto authenticate(String username, String password, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (AuthenticationException e) {
            throw new ApiException(INVALID_CREDENTIALS.message, INVALID_CREDENTIALS.code, INVALID_CREDENTIALS.status);
        }

        Member memberDetail = (Member) memberService.loadUserByUsername(username);

        String accessToken = jwtService.generateToken(Map.of("role", memberDetail.getRole().getName()), memberDetail.getUsername());
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // ⚠ true nếu HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtService.getExpiration() / 1000));

        response.addCookie(cookie);
        AuthResDto result = new AuthResDto();
        result.setAccessToken(accessToken);
        return result;
    }
}