package com.isikato.api.model.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LoginAuditModel {

    private long userId;
    private LocalDateTime timestamp;
}
