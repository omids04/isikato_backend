package com.isikato.fileutil.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FileInfo {

    private String mime;
    private String extension;
    private String name;
    private IsikatoFileType type;
}
