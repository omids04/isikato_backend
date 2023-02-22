package com.isikato.service.exceptions;

public class CategoryNotFoundException extends RuntimeException{
    public CategoryNotFoundException(long id) {
        super("category with id " + id + " does not exist");
    }
}
