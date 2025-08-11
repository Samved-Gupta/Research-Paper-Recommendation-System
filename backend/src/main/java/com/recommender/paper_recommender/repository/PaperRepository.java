package com.recommender.paper_recommender.repository;

import com.recommender.paper_recommender.model.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaperRepository extends JpaRepository<Paper, String> {
    // Spring Data JPA will provide all the necessary methods like count(), findAll(), etc.
}
