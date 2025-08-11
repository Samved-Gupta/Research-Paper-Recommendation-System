package com.recommender.paper_recommender.service;

import com.recommender.paper_recommender.dto.PaperDto;
import com.recommender.paper_recommender.dto.PersonalizedRequest;
import com.recommender.paper_recommender.model.ViewingHistory;
import com.recommender.paper_recommender.repository.ViewingHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.recommender.paper_recommender.dto.PaperPageDto;

@Service
public class RecommendationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String mlServiceUrl = "http://127.0.0.1:8000";

    private final ViewingHistoryRepository viewingHistoryRepository;

    @Autowired
    public RecommendationService(ViewingHistoryRepository viewingHistoryRepository) {
        this.viewingHistoryRepository = viewingHistoryRepository;
    }

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

        return Arrays.asList(response);
    }
}
