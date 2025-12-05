package com.BMS.backend.auth.service;

import com.BMS.backend.auth.dto.RegisterRequest;
import com.BMS.backend.auth.model.User;
import com.BMS.backend.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Register - Sign up
    public void register(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        String encodedPassword =

    }
}
