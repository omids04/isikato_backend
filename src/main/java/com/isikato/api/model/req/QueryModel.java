package com.isikato.api.model.req;

import com.fasterxml.jackson.databind.JsonNode;

public record QueryModel(int skip, int limit, JsonNode filter) {

}
