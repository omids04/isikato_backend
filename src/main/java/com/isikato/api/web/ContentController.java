package com.isikato.api.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.api.model.req.ContentCreateCommand;
import com.isikato.api.model.req.IdCommand;
import com.isikato.api.model.req.QueryModel;
import com.isikato.api.model.req.VisitsQueryModel;
import com.isikato.api.model.res.*;
import com.isikato.infrastructure.entities.Content;
import com.isikato.service.*;
import com.isikato.service.dtos.CollectionWithCount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import org.springframework.data.domain.Sort;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @PostMapping(value = "add",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ContentDetailModel createOrUpdate(@RequestBody @Valid ContentCreateCommand model, Authentication authentication) throws SQLException {
        var content = model.toEntity();
        return ContentDetailModel
                .fromEntity(contentService.createOrUpdate(content, (String) authentication.getPrincipal()));
    }

    @PostMapping(value = "remove",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public RemovedModel remove(@RequestBody IdCommand model){
        var result = contentService.remove(model.id());
        return new RemovedModel(result);
    }

    @PostMapping(value = "getInfo",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ContentWithSimilarModel getOne(@RequestBody IdCommand model){
        var result = contentService.getWithSimilar(model.id());
        return ContentWithSimilarModel.fromEntity(result.remove(0), result);
    }

    @PostMapping(value = "getAll",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CollectionWithTotalModel<ContentDetailModel> getList(@RequestBody(required = false) Optional<QueryModel> model){
        int limit = 10, skip = 0;
        JsonNode node = null;
        if(model.isPresent()){
            limit = model.get().limit() == 0 ? 10 : model.get().limit();
            skip = model.get().skip();
            node = model.get().filter();
        }
        var results = contentService.getAll(PageRequest.of(skip, limit, Sort.by("id").descending()), node);
        var data = results
                .getData()
                .stream()
                .map(ContentDetailModel::fromEntity)
                .collect(Collectors.toList());
        return new CollectionWithTotalModel<>(data, results.getTotal());
    }

    @PostMapping(value = "visits",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CollectionWithTotalModel<ContentDetailModel> getListByVisits(@RequestBody(required = false) Optional<VisitsQueryModel> model){
        CollectionWithCount<Content> contents;
        if(model.isEmpty()){
            contents = contentService.getAllByVisits(
                    PageRequest.of(0, 10),
                    null , LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT),
                    LocalDateTime.now());
        }
        else {
            var query = model.get();
            var limit = query.limit() == 0 ? 10 : query.limit();
            var start = query.startingFrom() == null? LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT) : query.startingFrom();
            var end = query.endingAt() == null? LocalDateTime.now() : query.endingAt();
            contents = contentService.getAllByVisits(PageRequest.of(query.skip(), limit), query.page(), start, end);
        }
        var data = contents
                .getData()
                .stream()
                .map(ContentDetailModel::fromEntity)
                .collect(Collectors.toList());
        return new CollectionWithTotalModel<>(data, contents.getTotal());
    }

    @GetMapping(value = "visits",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public VisitStatistics visits(
            @RequestParam(required = false) String page,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Integer limit) {

        var startTime = start == null? LocalDateTime.MIN : LocalDateTime.parse(start);
        var endTime = end == null? LocalDateTime.MAX : LocalDateTime.parse(end);
        var ac = contentService.getAllVisitsCount(startTime, endTime);
        limit = limit == null? 10 : limit;
        skip = skip == null? 0 : skip;
        var contents = contentService.getAllByVisits(PageRequest.of(skip, limit),page, LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT), LocalDateTime.now());
        return new VisitStatistics(
                ac,
                contents.getTotal(),
                contents.getData().stream().map(ContentDetailModel::fromEntity).collect(Collectors.toList())
        );
    }

    @PostMapping("downloads")
    public void updateDownloadCount(@RequestBody IdCommand idCommand){
        contentService.incDownloadCount(idCommand.id());
    }
}

