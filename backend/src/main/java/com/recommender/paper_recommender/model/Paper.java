package com.recommender.paper_recommender.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "papers")
@Getter
@Setter
@NoArgsConstructor
public class Paper {

    @Id
    @Column(name = "paper_id")
    private String paperId;

    @Column(name = "text_content", columnDefinition = "TEXT")
    private String textContent;

    @Column(name = "paper_index", nullable = false)
    private Integer paperIndex;
}
