package com.isikato.api.model.res;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AuthenticatedUserModel {

    private String token;
    private LocalDateTime expiration;
    private List<JsonNode> permissions;
}
