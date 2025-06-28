package com.sys.kanri.controllers;

import com.sys.kanri.controllers.base.BaseController;
import com.sys.kanri.dto.LoginRequestDto;
import com.sys.kanri.dto.RegisterRequestDto;
import com.sys.kanri.services.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final IAuthService authService;

    @GetMapping("test")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok("Tests");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto request){
        return ok(authService.register(request), "Đăng ký thành công");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        return ok(authService.authenticate(request), "Đăng nhập thành công");
    }
}
