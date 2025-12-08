package com.BMS.backend.service;

import com.BMS.backend.config.JwtTokenProvider;
import com.BMS.backend.dto.Auth.LoginRequest;
import com.BMS.backend.dto.Auth.RegisterRequest;
import com.BMS.backend.dto.Auth.TokenResponse;
import com.BMS.backend.domain.User;
import com.BMS.backend.exception.CustomException;
import com.BMS.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Transactional(readOnly = true)  // 클래스 레벨: 읽기 전용 트랜잭션
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
    @Transactional  // 쓰기 작업: DB에 사용자 저장
    public void register(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("이미 존재하는 이메일입니다.",
                    HttpStatus.CONFLICT);
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
            throw new CustomException(
                    "이메일 또는 비밀번호가 올바르지 않습니다.",HttpStatus.UNAUTHORIZED
            );
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(
                    "이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED
            );
        }
        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new TokenResponse(token, user.getId());
    }

    public boolean validateToken(String token){
        return  jwtTokenProvider.validateToken(token);
    }
}
