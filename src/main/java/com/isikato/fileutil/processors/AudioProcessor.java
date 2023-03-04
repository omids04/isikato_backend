package com.isikato.fileutil.processors;

import com.isikato.fileutil.model.AudioInfo;
import com.isikato.fileutil.model.MediaSizesBytes;
import com.isikato.fileutil.model.VideoInfo;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.UUID;

@Slf4j
@Component
public class AudioProcessor extends ContinuousMediaProcessor{


    public AudioProcessor(@Value("${path.ffprobe}") String probePath) throws IOException {
        super(probePath);
    }

    public AudioInfo processAudio(File in){
        var duration = getDuration(in.getPath());
        var size = getSize(in.getPath());
        return AudioInfo
                .builder()
                .duration(duration)
                .size(size)
                .build();
    }


}
