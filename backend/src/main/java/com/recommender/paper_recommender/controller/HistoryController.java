package com.recommender.paper_recommender.controller;

import com.recommender.paper_recommender.dto.HistoryDto;
import com.recommender.paper_recommender.model.User;
import com.recommender.paper_recommender.service.HistoryService;
import com.recommender.paper_recommender.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
@RestController
@RequestMapping("/api/history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private UserService userService;

    @PostMapping("/record")
    public ResponseEntity<?> recordViewingHistory(@RequestBody Map<String, String> payload, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "User is not authenticated."));
        }

        String userEmail = authentication.getName();
        User currentUser = userService.findUserByEmail(userEmail);
        Long userId = currentUser.getId();
        String paperId = payload.get("paperId");

        try {
            historyService.recordHistory(userId, paperId);
            return ResponseEntity.ok(Collections.singletonMap("message", "History recorded successfully."));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/view")
    public ResponseEntity<?> getHistory(Authentication authentication,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "User is not authenticated."));
        }

        String userEmail = authentication.getName();
        User currentUser = userService.findUserByEmail(userEmail);
        Long userId = currentUser.getId();

        Pageable pageable = PageRequest.of(page, size);
        Page<HistoryDto> history = historyService.getHistoryForUser(userId, pageable);
        return ResponseEntity.ok(history);
    }


    @DeleteMapping("/{historyId}")
    public ResponseEntity<?> deleteHistory(@PathVariable Long historyId, Authentication authentication) {
        String userEmail = authentication.getName();
        try {
            historyService.deleteHistoryEntry(userEmail, historyId);
            return ResponseEntity.ok(Map.of("message", "History entry removed successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
