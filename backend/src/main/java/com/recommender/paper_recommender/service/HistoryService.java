package com.recommender.paper_recommender.service;

import com.recommender.paper_recommender.dto.HistoryDto;
import com.recommender.paper_recommender.model.Paper;
import com.recommender.paper_recommender.model.User;
import com.recommender.paper_recommender.model.ViewingHistory;
import com.recommender.paper_recommender.repository.PaperRepository;
import com.recommender.paper_recommender.repository.UserRepository;
import com.recommender.paper_recommender.repository.ViewingHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.NoSuchElementException;

@Service
public class HistoryService {

    @Autowired
    private ViewingHistoryRepository viewingHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private UserService userService;

    public void recordHistory(Long userId, String paperId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new NoSuchElementException("Paper not found with ID: " + paperId));

        ViewingHistory newHistoryEntry = new ViewingHistory(user, paper);
        viewingHistoryRepository.save(newHistoryEntry);
    }

    @Transactional(readOnly = true)
    public Page<HistoryDto> getHistoryForUser(Long userId, Pageable pageable) {
        Page<ViewingHistory> historyPage = viewingHistoryRepository.findByUserIdOrderByViewedAtDesc(userId, pageable);
        return historyPage.map(HistoryDto::new);
    }

    @Transactional
    public void deleteHistoryEntry(String userEmail, Long historyId) {
        User user = userService.findUserByEmail(userEmail);
        ViewingHistory historyEntry = viewingHistoryRepository.findById(historyId)
                .orElseThrow(() -> new NoSuchElementException("History entry not found."));

        // Security check: ensure the history entry belongs to the current user
        if (!historyEntry.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Access denied to delete this history entry.");
        }

        viewingHistoryRepository.deleteById(historyId);
    }
}