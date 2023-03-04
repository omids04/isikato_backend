package com.isikato.fileutil.processors;

import com.isikato.fileutil.model.MediaSizesBytes;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.UUID;

@Component
@Slf4j
public class ImageProcessor extends MediaProcessor{

    private final FFmpegExecutor executor;

    public ImageProcessor(@Value("${path.ffprobe}") String probePath, @Value("${path.ffmpeg}")  String mpegPath) throws IOException {
        var ffmpeg = new FFmpeg(mpegPath);
        var ffprobe = new FFprobe(probePath);
        executor = new FFmpegExecutor(ffmpeg, ffprobe);
    }

    public MediaSizesBytes resize(File in, String ex){
        return MediaSizesBytes
                .builder()
                .large(resize(in.getPath(), 1440, ex))
                .medium(resize(in.getPath(), 1024, ex))
                .small(resize(in.getPath(), 700, ex))
                .thumb(resize(in.getPath(), 400, ex))
                .mini(resize(in.getPath(), 70, ex))
                .build();

    }

    private byte[]  resize(String in, int size, String ex){
        var pathToOut = "/tmp/" + UUID.randomUUID() + ex;
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(in)
                .addOutput(pathToOut)
                .setVideoResolution(size, size)
                .done();
        executor.createJob(builder).run();
        return getBytesAndDelete(pathToOut);
    }
}
