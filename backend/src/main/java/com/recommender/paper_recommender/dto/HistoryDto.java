package com.recommender.paper_recommender.dto;

import com.recommender.paper_recommender.model.ViewingHistory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class HistoryDto {
    private Long historyId;
    private String paperId;
    private String paperTitle;
    private LocalDateTime viewedAt;

    public HistoryDto(ViewingHistory history) {
        this.historyId = history.getHistoryId();
        this.paperId = history.getPaper().getPaperId();
        this.paperTitle = history.getPaper().getTextContent().split("  ")[0];
        this.viewedAt = history.getViewedAt();
    }
}