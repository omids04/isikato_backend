package com.isikato.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.infrastructure.entities.*;
import com.isikato.infrastructure.repositories.*;
import com.isikato.service.dtos.CollectionWithCount;
import com.isikato.service.exceptions.ContentException;
import com.isikato.service.specs.ContentSimilarSpecs;
import com.isikato.service.specs.ContentSpecs;
import com.isikato.service.specs.ContentVisitSpecs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final CategoryRepository categoryRepository;
    private final FileRepository fileRepository;
    private final ContentVisitService contentVisitService;
    private final ContentVisitRepository visitRepository;
    private final EmployeeRepository employeeRepository;

    public Content createOrUpdate(Content content, String username){
        Employee writer;
        if (content.getId() != 0L) {
            var contentOptional = contentRepository.findById(content.getId());
            if(contentOptional.isEmpty())
                throw new ContentException("content with id " + content.getId() + " does not exists!");
            writer = contentOptional.get().getWriter();
        }else {
            content.setPublished(true);
            writer = employeeRepository.findByUsername(username).orElse(null);
        }
        content.setWriter(writer);
        List<IsikatoFile> isikatoFiles = fileRepository
                .findAllById(content.getFiles().stream().map(IsikatoFile::getId).collect(Collectors.toList()));
        var categories = categoryRepository
                .findAllById(content.getCategories().stream().map(Category::getId).collect(Collectors.toList()));
        isikatoFiles.forEach(file -> file.setContent(content));
        content.setFiles(isikatoFiles);
        content.setCategories((List<Category>) categories);
        return contentRepository.save(content);
    }

    public boolean remove(long id) {
        var content = contentRepository.findById(id);
        if(content.isEmpty())
            return false;
        contentRepository.delete(content.get());
        return true;
    }

    @Transactional
    public CollectionWithCount<Content> getAll(PageRequest pageReq, JsonNode filter){
        var page = contentRepository
                .findAll(ContentSpecs.instance().fromFilter(filter), pageReq);
        var count = page.getTotalElements();
	var data = page.stream().peek(content -> content.setVisitCounter(content.getVisits().size())).toList();
        return new CollectionWithCount<>(data , count);
    }

    @Transactional
    public List<Content> getWithSimilar(long id) {
        var content = contentRepository.findById(id);
        if (content.isEmpty())
            throw new ContentException("content with id " + id + " does not exist");
	    var con = content.get();
        var visitedCount = visitRepository.countByContent_Id(con.getId());
        con.setVisitCounter(visitedCount);
        var specs = ContentSimilarSpecs
                .instance()
                .filter(content.get().getTags(),
                        content.get().getCategories().stream().map(Category::getId).collect(Collectors.toList()),
                        content.get().getPage(), content.get().getId());
        var contents = contentRepository.findAll(specs, Pageable.ofSize(5));
        var filtered = new ArrayList<Content>();
        filtered.add(con);
        filtered.addAll(contents.getContent());
        contentVisitService.addVisitForContent(con);
        return filtered;
    }

    public CollectionWithCount<Content> getAllByVisits(PageRequest pageReq, String page, LocalDateTime start, LocalDateTime end) {
        var contents = contentRepository.findAll();
        var results = new ArrayList<Content>();
        contents.forEach(content -> {
            if(page == null || page.equals(content.getPage())){
                long count = content.getVisits().stream()
                        .filter(cv -> cv.getTimestamp().isAfter(start))
                        .filter(cv -> cv.getTimestamp().isBefore(end))
                        .count();
                content.setVisitCounter(count);
                results.add(content);
            }
        });
        results.sort(Comparator.comparingLong(Content::getVisitCounter).reversed());
        var finalContents = results.stream().skip((long) pageReq.getPageNumber() * pageReq.getPageSize()).limit(pageReq.getPageSize()).toList();
        return new CollectionWithCount<>(finalContents, results.size());
    }

    public Map<String, Integer> getAllVisitsCount(LocalDateTime start, LocalDateTime end) {
        var vc = new HashMap<>(Map.of(
                "applications", 0,
                "books", 0,
                "images", 0,
                "videos", 0,
                "sounds", 0,
                "total", 0));

        contentRepository
                .findAll()
                .forEach(content -> {
                    content.getVisits()
                            .stream()
                            .map(ContentVisit::getTimestamp)
                            .filter(start::isBefore)
                            .filter(end::isAfter)
                            .forEach(visit -> {
                        vc.replace("total", vc.get("total") + 1);
                        vc.replace(content.getPage(), vc.get(content.getPage()) + 1);
                    });
                });

        return vc;
    }

     @Transactional
    public void incDownloadCount(long id) {
        var content = contentRepository.findById(id);
        if (content.isEmpty())
            throw new ContentException("content with id " + id + " does not exist");
        content.get().setDownloadCounter(content.get().getDownloadCounter() + 1);

    }
}
