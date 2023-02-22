package com.isikato.api.model.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CollectionWithTotalModel<T> {

    private List<T> data;
    private long total;
}
