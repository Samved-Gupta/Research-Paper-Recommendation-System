package com.recommender.paper_recommender.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaperPageDto {
    private List<PaperDto> content;
    private int totalPages;
    private int number;
    private int size;
    private boolean first;
    private boolean last;
}