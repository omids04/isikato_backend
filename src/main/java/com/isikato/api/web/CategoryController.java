package com.isikato.api.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.api.model.req.CategoryCreateCommand;
import com.isikato.api.model.req.IdCommand;
import com.isikato.api.model.req.QueryModel;
import com.isikato.api.model.res.CategoryDetailsModel;
import com.isikato.api.model.res.CollectionWithTotalModel;
import com.isikato.api.model.res.IsikatoErrorModel;
import com.isikato.api.model.res.RemovedModel;
import com.isikato.api.util.RequestUtil;
import com.isikato.service.CategoryService;
import com.isikato.service.exceptions.CategoryNamingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.awt.print.Pageable;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping(value = "add",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDetailsModel createOrUpdate(@RequestBody @Valid CategoryCreateCommand model){
        return CategoryDetailsModel
                .fromDto(categoryService.createOrUpdate(model.toDto()));
    }

    @PostMapping(value = "remove",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public RemovedModel remove(@RequestBody IdCommand model){
        var result = categoryService.remove(model.id());
        return new RemovedModel(result);
    }

    @PostMapping(value = "getInfo",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CategoryDetailsModel getOne(@RequestBody IdCommand model){
        var result = categoryService.get(model.id());
        return CategoryDetailsModel.fromDto(result);
    }

    @PostMapping(value = "getAll",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CollectionWithTotalModel<CategoryDetailsModel> getList(@RequestBody(required = false) Optional<QueryModel> model){
        int limit = 10, skip = 0;
        JsonNode node = null;
        if(model.isPresent()){
            limit = model.get().limit() == 0 ? 10 : model.get().limit();
            skip = model.get().skip();
            node = model.get().filter();
        }
        var results = categoryService.getAll(PageRequest.of(skip, limit), node);
        var cats = results
                .getData()
                .stream()
                .map(CategoryDetailsModel::fromDto)
                .collect(Collectors.toList());
        return new CollectionWithTotalModel<>(cats, results.getTotal());
    }
    @ExceptionHandler(CategoryNamingException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public IsikatoErrorModel handleBadRequestException(CategoryNamingException ex, HttpServletRequest request){
        var msg = ex.getMessage();
        return IsikatoErrorModel.createWithCurrentTime(msg, RequestUtil.getPath(request));
    }

}
