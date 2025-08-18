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

    // Constructor to initialize fields
    public PaperDto(String id, Integer index, String text_content) {
        this.id = id;
        this.index = index;
        this.text_content = text_content;
    }

    // Default no-arg constructor (needed by Jackson / JPA sometimes)
    public PaperDto() {
    }
}
