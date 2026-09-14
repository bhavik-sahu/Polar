package com.polar.logistics.service;

import com.polar.logistics.dto.AuthDtos;
import com.polar.logistics.entity.User;
import com.polar.logistics.entity.enums.UserRole;
import com.polar.logistics.exception.ValidationConflictException;
import com.polar.logistics.repository.UserRepository;
import com.polar.logistics.security.JwtTokenProvider;
import com.polar.logistics.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationConflictException("Username '" + request.getUsername() + "' is already taken");
        }

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : UserRole.EXPEDITION_MEMBER)
                .station(request.getStation())
                .build();

        User savedUser = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String jwt = tokenProvider.generateToken(authentication);

        return AuthDtos.AuthResponse.builder()
                .token(jwt)
                .user(mapToUserDto(savedUser))
                .build();
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(principal.getId()).orElseThrow();

        return AuthDtos.AuthResponse.builder()
                .token(jwt)
                .user(mapToUserDto(user))
                .build();
    }

    public AuthDtos.UserDto getCurrentUserProfile(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId()).orElseThrow();
        return mapToUserDto(user);
    }

    private AuthDtos.UserDto mapToUserDto(User user) {
        return AuthDtos.UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .station(user.getStation())
                .build();
    }
}
