package com.isikato.service.exceptions;

public class CategoryNamingException extends RuntimeException {
    public CategoryNamingException(String name) {
        super("category with name " + name + " already exist!");
    }
}
