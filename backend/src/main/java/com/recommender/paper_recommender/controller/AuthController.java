package com.recommender.paper_recommender.controller;

import com.recommender.paper_recommender.dto.LoginRequest;
import com.recommender.paper_recommender.dto.LoginResponse;
import com.recommender.paper_recommender.dto.RegistrationRequest;
import com.recommender.paper_recommender.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
// ... other imports
import org.springframework.http.HttpStatus;
import com.recommender.paper_recommender.dto.PasswordResetRequest;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        try {
            userService.registerNewUser(registrationRequest);
            Map<String, String> response = Collections.singletonMap("message", "User registered successfully!");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, String> errorResponse = Collections.singletonMap("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            String token = userService.loginUser(loginRequest);
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetRequest request) {
        try {
            userService.createPasswordResetTokenForUser(request.getEmail());
            return ResponseEntity.ok(Map.of("message", "A password reset link has been sent to your email."));
        } catch (NoSuchElementException e) {
            // IMPORTANT: Do not reveal if an email exists or not for security reasons.
            return ResponseEntity.ok(Map.of("message", "If an account with that email exists, a reset link has been sent."));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam    ("token") String token, @RequestBody PasswordResetRequest request) {
        try {
            userService.changeUserPassword(token, request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password has been successfully reset."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
