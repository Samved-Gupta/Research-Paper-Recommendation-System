package com.recommender.paper_recommender.repository;

import com.recommender.paper_recommender.model.SavedPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavedPaperRepository extends JpaRepository<SavedPaper, Long> {

    // Finds all papers saved by a specific user, ordered by the most recently saved
    Page<SavedPaper> findByUserIdOrderBySavedAtDesc(Long userId, Pageable pageable);
    // Finds a specific saved paper entry for a given user and paper
    Optional<SavedPaper> findByUserIdAndPaperPaperId(Long userId, String paperId);
}