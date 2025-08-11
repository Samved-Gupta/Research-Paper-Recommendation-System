package com.recommender.paper_recommender.repository;

import com.recommender.paper_recommender.model.ViewingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Repository
public interface ViewingHistoryRepository extends JpaRepository<ViewingHistory, Long> {

    // This is the standard method for getting history for the history page, sorted by newest first.
    Page<ViewingHistory> findByUserIdOrderByViewedAtDesc(Long userId, Pageable pageable);

    // This is the method for getting history for personalized recommendations.
    @Query("SELECT vh FROM ViewingHistory vh JOIN FETCH vh.paper p WHERE vh.user.id = :userId ORDER BY vh.viewedAt DESC")
    List<ViewingHistory> findUserHistoryWithPapers(@Param("userId") Long userId);
}
