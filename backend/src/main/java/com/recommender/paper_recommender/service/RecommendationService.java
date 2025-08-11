package com.recommender.paper_recommender.service;

import com.recommender.paper_recommender.dto.PaperDto;
import com.recommender.paper_recommender.dto.PaperPageDto;
import com.recommender.paper_recommender.dto.PersonalizedRequest;
import com.recommender.paper_recommender.model.ViewingHistory;
import com.recommender.paper_recommender.repository.ViewingHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Use @Value to inject the URL from the docker-compose environment variable
    @Value("${ML_SERVICE_URL}")
    private String mlServiceUrl;

    private final ViewingHistoryRepository viewingHistoryRepository;

    @Autowired
    public RecommendationService(ViewingHistoryRepository viewingHistoryRepository) {
        this.viewingHistoryRepository = viewingHistoryRepository;
    }

    // Updated for pagination
    public PaperPageDto searchPapers(String query, int page, int size) {
        String url = mlServiceUrl + "/search/?query=" + query + "&page=" + page + "&size=" + size;
        return restTemplate.getForObject(url, PaperPageDto.class);
    }

    public List<PaperDto> findSimilarPapers(int paperIndex) {
        String url = mlServiceUrl + "/recommend/?paper_index=" + paperIndex;
        PaperDto[] response = restTemplate.getForObject(url, PaperDto[].class);
        return Arrays.asList(response);
    }

    @Transactional(readOnly = true)
    public List<PaperDto> getPersonalizedRecommendations(Long userId) {
        List<ViewingHistory> history = viewingHistoryRepository.findUserHistoryWithPapers(userId);

        if (history.isEmpty()) {
            return List.of();
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