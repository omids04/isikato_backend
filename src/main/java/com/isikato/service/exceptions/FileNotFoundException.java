package com.isikato.service.exceptions;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(long id) {
        super("could not find file with id: " + id);
    }
}
