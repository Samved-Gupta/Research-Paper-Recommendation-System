package com.recommender.paper_recommender.controller;

import com.recommender.paper_recommender.dto.PaperDto;
import com.recommender.paper_recommender.model.User;
import com.recommender.paper_recommender.service.RecommendationService;
import com.recommender.paper_recommender.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.recommender.paper_recommender.dto.PaperPageDto;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public ResponseEntity<PaperPageDto> search(@RequestParam String query,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        try {
            PaperPageDto papers = recommendationService.searchPapers(query, page, size);
            return ResponseEntity.ok(papers);
        } catch (Exception e) {
            logger.error("Error during search for query: {}", query, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<PaperDto>> recommend(@RequestParam int paper_index) {
        try {
            List<PaperDto> papers = recommendationService.findSimilarPapers(paper_index);
            return ResponseEntity.ok(papers);
        } catch (Exception e) {
            logger.error("Error during recommend for index: {}", paper_index, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/personalized")
    public ResponseEntity<?> getPersonalized(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "User is not authenticated."));
        }

        try {
            String userEmail = authentication.getName();
            User currentUser = userService.findUserByEmail(userEmail);
            Long userId = currentUser.getId();

            logger.info("Received request for personalized recommendations for user ID: {}", userId);
            List<PaperDto> recommendations = recommendationService.getPersonalizedRecommendations(userId);
            return ResponseEntity.ok(recommendations);

        } catch (Exception e) {
            logger.error("CRITICAL ERROR fetching personalized recommendations:", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

