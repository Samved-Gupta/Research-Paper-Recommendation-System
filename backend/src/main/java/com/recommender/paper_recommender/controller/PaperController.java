package com.recommender.paper_recommender.controller;

import com.recommender.paper_recommender.dto.PaperDto;
import com.recommender.paper_recommender.model.Paper;
import com.recommender.paper_recommender.repository.PaperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/papers")
public class PaperController {

    @Autowired
    private PaperRepository paperRepository;

    @GetMapping("/popular")
    public ResponseEntity<List<PaperDto>> getPopularPapers() {
        // For now, "popular" will just be the first 10 papers in the database.
        Page<Paper> paperPage = paperRepository.findAll(PageRequest.of(0, 10));
        List<PaperDto> paperDtos = paperPage.getContent().stream().map(paper -> {
            PaperDto dto = new PaperDto();
            dto.setId(paper.getPaperId());
            dto.setIndex(paper.getPaperIndex());
            dto.setText_content(paper.getTextContent());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(paperDtos);
    }
}