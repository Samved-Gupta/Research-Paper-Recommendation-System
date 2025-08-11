package com.recommender.paper_recommender.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.recommender.paper_recommender.model.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    // Find all login history entries for a user, sorted by newest first
    Page<LoginHistory> findByUserIdOrderByLoginTimeDesc(Long userId, Pageable pageable);

}