package com.isikato.service.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class CollectionWithCount<T> {

    private List<T> data;
    private long total;
}
