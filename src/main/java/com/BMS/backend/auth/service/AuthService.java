package com.BMS.backend.auth.service;

import com.BMS.backend.auth.config.JwtTokenProvider;
import com.BMS.backend.auth.dto.LoginRequest;
import com.BMS.backend.auth.dto.RegisterRequest;
import com.BMS.backend.auth.dto.TokenResponse;
import com.BMS.backend.auth.model.User;
import com.BMS.backend.auth.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Register - Sign up
    public void register(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(encodedPassword)
                .build();
        userRepository.save(user);
    }

    // Login
    public TokenResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }
        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new TokenResponse(token);
    }

    public boolean validateToken(String token){
        return  jwtTokenProvider.validateToken(token);
    }
}
