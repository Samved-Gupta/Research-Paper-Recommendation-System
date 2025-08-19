package com.recommender.paper_recommender.service;

import com.recommender.paper_recommender.dto.PaperDto;
import com.recommender.paper_recommender.dto.PaperPageDto;
import com.recommender.paper_recommender.dto.PersonalizedRequest;
import com.recommender.paper_recommender.model.ViewingHistory;
import com.recommender.paper_recommender.repository.ViewingHistoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    // Declare all fields together and make them final
    private final RestTemplate restTemplate;
    private final ViewingHistoryRepository viewingHistoryRepository;

    @Value("${ml.service.url}")
    private String mlServiceUrl;

    public RecommendationService(RestTemplate restTemplate, ViewingHistoryRepository viewingHistoryRepository) {
        this.restTemplate = restTemplate;
        this.viewingHistoryRepository = viewingHistoryRepository;
    }

    public PaperPageDto searchPapers(String query, int page, int size) {
        String url = mlServiceUrl + "/search/?q=" + query + "&page=" + page + "&size=" + size;
        return restTemplate.getForObject(url, PaperPageDto.class);
    }

    public List<PaperDto> findSimilarPapers(int paperIndex) {
        String url = mlServiceUrl + "/recommend/?paper_index=" + paperIndex;
        PaperDto[] response = restTemplate.getForObject(url, PaperDto[].class);
        return response != null ? Arrays.asList(response) : List.of();
    }

    @Transactional(readOnly = true)
    public List<PaperDto> getPersonalizedRecommendations(Long userId) {
        List<ViewingHistory> history = viewingHistoryRepository.findUserHistoryWithPapers(userId);

        if (history.isEmpty()) {
            return List.of(); // Return empty list if no history
        }

        List<Integer> paperIndices = history.stream()
                .map(h -> h.getPaper().getPaperIndex())
                .collect(Collectors.toList());

        String url = mlServiceUrl + "/recommend/personalized/";
        PersonalizedRequest requestPayload = new PersonalizedRequest(paperIndices);
        PaperDto[] response = restTemplate.postForObject(url, requestPayload, PaperDto[].class);

        return response != null ? Arrays.asList(response) : List.of();
    }
}