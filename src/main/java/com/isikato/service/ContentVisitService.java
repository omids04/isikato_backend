package com.isikato.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.infrastructure.entities.Content;
import com.isikato.infrastructure.entities.ContentVisit;
import com.isikato.infrastructure.repositories.ContentVisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentVisitService {

    private final ContentVisitRepository visitRepository;

    @Async
    public void addVisitForContent(Content content){
        var visit = ContentVisit.builder().content(content).build();
        visitRepository.save(visit);
    }

    public void getAll(PageRequest of, JsonNode node) {

    }
}
