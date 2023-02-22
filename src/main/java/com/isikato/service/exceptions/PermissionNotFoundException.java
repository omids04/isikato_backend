package com.isikato.service.exceptions;

public class PermissionNotFoundException extends RuntimeException{
    private static String MSG = "Permission With Id %s Does Not Exist";

    public PermissionNotFoundException(long id) {
        super(MSG.formatted(id));
    }
}
