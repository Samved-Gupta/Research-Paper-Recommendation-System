package com.recommender.paper_recommender.service;

import com.recommender.paper_recommender.dto.SavedPaperDto;
import com.recommender.paper_recommender.model.Paper;
import com.recommender.paper_recommender.model.SavedPaper;
import com.recommender.paper_recommender.model.User;
import com.recommender.paper_recommender.repository.PaperRepository;
import com.recommender.paper_recommender.repository.SavedPaperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Service
public class BookmarkService {

    @Autowired
    private SavedPaperRepository savedPaperRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PaperRepository paperRepository;

    @Transactional
    public void savePaper(String userEmail, String paperId) {
        User user = userService.findUserByEmail(userEmail);
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new NoSuchElementException("Paper not found with ID: " + paperId));

        if (savedPaperRepository.findByUserIdAndPaperPaperId(user.getId(), paperId).isPresent()) {
            throw new IllegalStateException("Paper is already saved.");
        }

        SavedPaper savedPaper = new SavedPaper(user, paper);
        savedPaperRepository.save(savedPaper);
    }

    @Transactional
    public void unsavePaper(String userEmail, String paperId) {
        User user = userService.findUserByEmail(userEmail);

        // Use the new, correct method name here as well
        SavedPaper savedPaper = savedPaperRepository.findByUserIdAndPaperPaperId(user.getId(), paperId)
                .orElseThrow(() -> new NoSuchElementException("Saved paper entry not found."));

        savedPaperRepository.delete(savedPaper);
    }

    @Transactional(readOnly = true)
    public Page<SavedPaperDto> getSavedPapers(String userEmail, Pageable pageable) {
        User user = userService.findUserByEmail(userEmail);
        Page<SavedPaper> savedPapersPage = savedPaperRepository.findByUserIdOrderBySavedAtDesc(user.getId(), pageable);
        return savedPapersPage.map(SavedPaperDto::new);
    }
}