package com.example.women_safety.service;

import com.example.women_safety.dto.LoginRequest;
import com.example.women_safety.dto.RegisterRequest;
import com.example.women_safety.dto.UserResponse;
import com.example.women_safety.entity.User;
import com.example.women_safety.repository.UserRepository;
import com.example.women_safety.security.JwtService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email already exists!";
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return "User Registered Successfully!";
    }

    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return "User Not Found!";
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return "Incorrect Password!";
        }

        return jwtService.generateToken(request.getEmail());
    }

    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) return null;

        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());

        return res;
    }

    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) return null;

        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());

        return res;
    }

}
