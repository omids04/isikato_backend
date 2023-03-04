package com.isikato.api.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.api.model.req.BannerCreateCommand;
import com.isikato.api.model.req.IdCommand;
import com.isikato.api.model.req.QueryModel;
import com.isikato.api.model.res.*;
import com.isikato.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @PostMapping(value = "add",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BannerDetailsModel createOrUpdate(@RequestBody @Valid BannerCreateCommand model){
        return BannerDetailsModel
                .fromDto(bannerService.createOrUpdate(model.toDto()));
    }

    @PostMapping(value = "remove",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public RemovedModel remove(@RequestBody IdCommand model){
        var result = bannerService.remove(model.id());
        return new RemovedModel(result);
    }

    @PostMapping(value = "getInfo",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public BannerDetailsModel getOne(@RequestBody IdCommand model){
        var result = bannerService.get(model.id());
        return BannerDetailsModel.fromDto(result);
    }

    @PostMapping(value = "getAll",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CollectionWithTotalModel<BannerDetailsModel> getList(@RequestBody(required = false) Optional<QueryModel> model){
        int limit = 10, skip = 0;
        JsonNode node = null;
        if(model.isPresent()){
            limit = model.get().limit() == 0 ? 10 : model.get().limit();
            skip = model.get().skip();
            node = model.get().filter();
        }
        var results = bannerService.getAll(PageRequest.of(skip, limit), node);
        var data = results
                .getData()
                .stream()
                .map(BannerDetailsModel::fromDto)
                .collect(Collectors.toList());
        return new CollectionWithTotalModel<>(data, results.getTotal());
    }

}
