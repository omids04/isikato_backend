package com.isikato.fileutil.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VideoInfo {
    private long fileSize;
    private byte[] cover;
    private int coverTime;
    private double duration;
}
