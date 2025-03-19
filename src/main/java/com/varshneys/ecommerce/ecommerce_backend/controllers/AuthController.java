package com.varshneys.ecommerce.ecommerce_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.security.oauth2.core.oidc.user.OidcUser;
// import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// import com.varshneys.ecommerce.ecommerce_backend.Model.Role;
import com.varshneys.ecommerce.ecommerce_backend.Model.User;
import com.varshneys.ecommerce.ecommerce_backend.payload.AuthRequest;
import com.varshneys.ecommerce.ecommerce_backend.payload.AuthResponse;
import com.varshneys.ecommerce.ecommerce_backend.repository.UserRepository;
import com.varshneys.ecommerce.ecommerce_backend.security.JwtUtil;
import com.varshneys.ecommerce.ecommerce_backend.security.UserDetailImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
// @CrossOrigin(origins = "http://localhost:5173")
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
        String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getUserId(), userDetails.getUsername()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully"); 
    }

    // oauth login
    // @GetMapping("/google/success")
    // public ResponseEntity<AuthResponse> googleLogin(@AuthenticationPrincipal OidcUser user) {
    //     String email = user.getEmail();
        
    //     User existingUser = userRepository.findByEmail(email)
    //     .orElseGet(() -> {
    //         User newUser = new User();
    //         newUser.setEmail(email);
    //         newUser.setPassword(null); 
    //         newUser.setRole(Role.USER);
    //         return userRepository.save(newUser);
    //     });

    //     String jwt = jwtUtil.generateToken(email);
    //     return ResponseEntity.ok(new AuthResponse(jwt, existingUser.getUserId(), email));
    // }
    @GetMapping("/id")
    public ResponseEntity<Long> getUserId(@RequestParam("email") String email) { 
        // logger.info("Received email: {}", email);

        Long userId = userRepository.getUserIdByEmail(email);
        if (userId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userId);
    }
}
