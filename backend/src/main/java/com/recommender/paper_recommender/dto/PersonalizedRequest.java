package com.recommender.paper_recommender.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PersonalizedRequest {
    private List<Integer> paper_indices;
}