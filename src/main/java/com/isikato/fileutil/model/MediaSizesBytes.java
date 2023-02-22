package com.isikato.fileutil.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MediaSizesBytes {

    private byte[] mini;
    private byte[] thumb;
    private byte[] small;
    private byte[] medium;
    private byte[] large;
    private byte[] huge;

}
