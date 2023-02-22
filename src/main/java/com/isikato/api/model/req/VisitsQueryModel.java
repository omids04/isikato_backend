package com.isikato.api.model.req;

import java.time.LocalDateTime;

public record VisitsQueryModel(int skip, int limit, String page, LocalDateTime startingFrom, LocalDateTime endingAt) {

}
