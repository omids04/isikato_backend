package com.isikato.api.model.res;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class IsikatoErrorModel {

    private String path;
    private String message;
    private LocalDateTime timestamp;

    public static IsikatoErrorModel createWithCurrentTime(String msg, String path){
        return IsikatoErrorModel
                .builder()
                .timestamp(LocalDateTime.now())
                .message(msg)
                .path(path)
                .build();
    }
}
