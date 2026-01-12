package com.sys.kanri.controllers;

import com.sys.kanri.controllers.base.BaseController;
import com.sys.kanri.dto.request.LoginReqDto;
import com.sys.kanri.dto.request.RegisterReqDto;
import com.sys.kanri.dto.response.AuthResDto;
import com.sys.kanri.services.AuthService;
import com.sys.kanri.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@Validated
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API đăng nhập và đăng ký")
public class AuthController extends BaseController {
    @Value("${jwt.expiration}")
    private long jwtExpiration; // Inject từ yml
    @Value("${jwt.refresh-expiration}") // Thêm cấu hình này vào application.yml (VD: 604800000 = 7 ngày)
    private long refreshExpiration;

    private final AuthService authService;
    private final MemberService memberService;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập hệ thống", description = "Trả về Access Token và Refresh Token")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReqDto request, HttpServletResponse response) {
        AuthResDto result = authService.authenticate(request.getUsername(), request.getPassword());

        // 1. Set Access Token Cookie
        setCookie(response, "accessToken", result.getAccessToken(), (int) (jwtExpiration / 1000));

        // 2. Set Refresh Token Cookie (Quan trọng: HttpOnly, Path chỉ định)
        Cookie refreshCookie = new Cookie("refreshToken", result.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // True nếu chạy HTTPS
        refreshCookie.setPath("/auth/refresh"); // Chỉ gửi cookie này khi gọi đúng API refresh
        refreshCookie.setMaxAge((int) (refreshExpiration / 1000));
        response.addCookie(refreshCookie);

        return ok(result, "Đăng nhập thành công");
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký thành viên mới")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReqDto request) {
        memberService.registerMember(request, null);
        return created(null, "Đăng kí thành công!");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookieValue(request, "refreshToken");

        // Gọi service xử lý logic
        AuthResDto result = authService.refreshToken(refreshToken);

        // Cập nhật lại Access Token Cookie mới
        setCookie(response, "accessToken", result.getAccessToken(), (int) (jwtExpiration / 1000));

        return ok(result, "Làm mới token thành công");
    }

    /**
     * Sets a cookie with the specified name, value, and expiration time on the HTTP response.
     * The cookie is configured to be HTTP-only and is accessible on the root path.
     *
     * @param response the HttpServletResponse object to which the cookie will be added
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param maxAge the maximum age of the cookie in seconds
     */
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    // Helper method để lấy value từ cookie
    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> name.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}