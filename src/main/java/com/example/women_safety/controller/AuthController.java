package com.example.women_safety.controller;

import com.example.women_safety.dto.LoginRequest;
import com.example.women_safety.dto.RegisterRequest;
import com.example.women_safety.dto.UserResponse;
import com.example.women_safety.service.AuthService;
import com.example.women_safety.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;   // ✅ FIXED (added)

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/user/{id}")
    public Object getUser(@PathVariable Long id) {
        UserResponse response = authService.getUserById(id);
        if (response == null) {
            return "User Not Found!";
        }
        return response;
    }

    @GetMapping("/me")
    public Object getUserFromToken(@RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "Invalid Token!";
        }

        String token = authHeader.substring(7);  // remove 'Bearer '

        String email = jwtService.extractEmail(token);  // ✅ NOW WORKS

        UserResponse res = authService.getUserByEmail(email);

        if (res == null) return "User Not Found!";

        return res;
    }
}
