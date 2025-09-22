package com.varshneys.ecommerce.ecommerce_backend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.varshneys.ecommerce.ecommerce_backend.Model.Role;
import com.varshneys.ecommerce.ecommerce_backend.Model.User;
import com.varshneys.ecommerce.ecommerce_backend.payload.AuthRequest;
import com.varshneys.ecommerce.ecommerce_backend.payload.AuthResponse;
import com.varshneys.ecommerce.ecommerce_backend.payload.RegisterRequest;
import com.varshneys.ecommerce.ecommerce_backend.payload.UserResponse;
import com.varshneys.ecommerce.ecommerce_backend.repository.UserRepository;
import com.varshneys.ecommerce.ecommerce_backend.security.JwtUtil;
import com.varshneys.ecommerce.ecommerce_backend.security.UserDetailImpl;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
            UserDetailsService userDetailsService, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getRole());

        return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getUserId(), userDetails.getUsername(), userDetails.getRole()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken!");
        }

        // Check if role is provided
        if (request.getRole() == null || request.getRole().isBlank()) {
            return ResponseEntity.badRequest().body("Role is required. Only USER or SELLER are allowed.");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role. Only USER or SELLER are allowed.");
        }

        // Explicitly allow only USER or SELLER
        if (role != Role.USER && role != Role.SELLER) {
            return ResponseEntity.badRequest().body("Invalid role. Only USER or SELLER are allowed.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/id")
    public ResponseEntity<Long> getUserId(@RequestParam("email") String email) {
        // logger.info("Received email: {}", email);

        Long userId = userRepository.getUserIdByEmail(email);
        if (userId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userId);
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // System.out.println(authentication.toString()); 

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        UserDetailImpl userDetails = (UserDetailImpl) authentication.getPrincipal();

        UserResponse userResponse = new UserResponse(
            userDetails.getUserId(),
            userDetails.getName(),  
            userDetails.getUsername(),
            userDetails.getRole()
        );

        return ResponseEntity.ok(userResponse);
    }

}
