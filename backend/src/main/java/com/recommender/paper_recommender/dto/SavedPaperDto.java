package com.recommender.paper_recommender.dto;

import com.recommender.paper_recommender.model.SavedPaper;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SavedPaperDto {
    private String paperId;
    private String paperTitle;
    private LocalDateTime savedAt;

    public SavedPaperDto(SavedPaper savedPaper) {
        this.paperId = savedPaper.getPaper().getPaperId();
        // Extracts just the title from the full text content
        this.paperTitle = savedPaper.getPaper().getTextContent().split("  ")[0];
        this.savedAt = savedPaper.getSavedAt();
    }
}