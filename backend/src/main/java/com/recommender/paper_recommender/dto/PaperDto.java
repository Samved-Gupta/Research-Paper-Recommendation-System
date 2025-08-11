package com.recommender.paper_recommender.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaperDto {
    private String id;
    private String text_content;
    private Integer index;
}
