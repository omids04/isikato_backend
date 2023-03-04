package com.isikato.api.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.api.model.req.IdCommand;
import com.isikato.api.model.req.PermissionCreateCommand;
import com.isikato.api.model.req.QueryModel;
import com.isikato.api.model.res.CollectionWithTotalModel;
import com.isikato.api.model.res.PermissionDetails;
import com.isikato.api.model.res.RemovedModel;
import com.isikato.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping(value = "add",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public PermissionDetails createOrUpdate(@RequestBody PermissionCreateCommand model){
        return PermissionDetails
                .fromEntity(permissionService.createOrUpdate(model.toEntity()));
    }

    @PostMapping(value = "remove",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public RemovedModel remove(@RequestBody IdCommand model){
        var result = permissionService.remove(model.id());
        return new RemovedModel(result);
    }

    @PostMapping(value = "getInfo",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public PermissionDetails getOne(@RequestBody IdCommand model){
        var result = permissionService.get(model.id());
        return PermissionDetails.fromEntity(result);
    }

    @PostMapping(value = "getAll",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CollectionWithTotalModel<PermissionDetails> getList(@RequestBody(required = false) Optional<QueryModel> model){
        int limit = 10, skip = 0;
        JsonNode node = null;
        if(model.isPresent()){
            limit = model.get().limit() == 0 ? 10 : model.get().limit();
            skip = model.get().skip();
            node = model.get().filter();
        }
        var results = permissionService.getAll(PageRequest.of(skip, limit), node);
        var data = results
                .getData()
                .stream()
                .map(PermissionDetails::fromEntity)
                .collect(Collectors.toList());
        return new CollectionWithTotalModel<>(data, results.getTotal());
    }

}
