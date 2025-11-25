package com.sys.kanri.controllers;

import com.sys.kanri.controllers.base.BaseController;
import com.sys.kanri.dto.request.LoginReqDto;
import com.sys.kanri.dto.request.RegisterReqDto;
import com.sys.kanri.dto.response.AuthResDto;
import com.sys.kanri.services.AuthService;
import com.sys.kanri.services.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
@RequiredArgsConstructor
public class AuthController extends BaseController {
    private final AuthService authService;
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReqDto request, HttpServletResponse response) {
        AuthResDto result = authService.authenticate(request.getUsername(), request.getPassword(), response);
        return ok(result, "Đăng nhập thành công");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReqDto request) {
        memberService.registerMember(request, null);
        return created(null, "Đăng kí thành công!");
    }
}