package com.isikato.fileutil.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AudioInfo {

    private double duration;
    private long size;
}
