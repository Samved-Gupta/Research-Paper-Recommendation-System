package com.recommender.paper_recommender.controller;

import com.recommender.paper_recommender.dto.SavedPaperDto;
import com.recommender.paper_recommender.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @PostMapping("/{paperId:.+}")
    public ResponseEntity<?> savePaper(@PathVariable String paperId, Authentication authentication) {
        String userEmail = authentication.getName();
        try {
            bookmarkService.savePaper(userEmail, paperId);
            return ResponseEntity.ok(Map.of("message", "Paper saved successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{paperId:.+}")
    public ResponseEntity<?> unsavePaper(@PathVariable String paperId, Authentication authentication) {
        String userEmail = authentication.getName();
        try {
            bookmarkService.unsavePaper(userEmail, paperId);
            return ResponseEntity.ok(Map.of("message", "Paper unsaved successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Page<SavedPaperDto>> getSavedPapers(Authentication authentication,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        String userEmail = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<SavedPaperDto> savedPapers = bookmarkService.getSavedPapers(userEmail, pageable);
        return ResponseEntity.ok(savedPapers);
    }
}