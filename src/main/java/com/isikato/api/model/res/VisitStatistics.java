package com.isikato.api.model.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class VisitStatistics {

    private Map<String, Integer> accumulate;
    private long total;
    private List<ContentDetailModel> contents;



}