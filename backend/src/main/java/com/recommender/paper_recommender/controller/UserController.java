package com.recommender.paper_recommender.controller;

import com.recommender.paper_recommender.dto.LoginHistoryDto;
import com.recommender.paper_recommender.dto.PasswordChangeRequest;
import com.recommender.paper_recommender.dto.ProfileDto;
import com.recommender.paper_recommender.model.User;
import com.recommender.paper_recommender.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile(Authentication authentication) {
        String userEmail = authentication.getName();
        User currentUser = userService.findUserByEmail(userEmail);
        return ResponseEntity.ok(new ProfileDto(currentUser));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        User currentUser = userService.findUserByEmail(userEmail);
        Long userId = currentUser.getId();

        try {
            userService.changePassword(userId, request);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/login-history")
    public ResponseEntity<Page<LoginHistoryDto>> getLoginHistory(Authentication authentication,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        String userEmail = authentication.getName();
        User currentUser = userService.findUserByEmail(userEmail);
        Long userId = currentUser.getId();

        Pageable pageable = PageRequest.of(page, size);
        Page<LoginHistoryDto> history = userService.getLoginHistoryForUser(userId, pageable);
        return ResponseEntity.ok(history);
    }
}
