package com.isikato.fileutil.exceptions;

public class FileTypeDetectionException extends RuntimeException{

    public FileTypeDetectionException() {
        super("Problem in detecting file type");
    }
}
