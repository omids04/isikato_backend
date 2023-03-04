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
public class VideoProcessor extends ContinuousMediaProcessor{

    private final FFmpegExecutor executor;

    public VideoProcessor(@Value("${path.ffprobe}") String probePath,@Value("${path.ffmpeg}")  String mpegPath) throws IOException {
        super(probePath);
        var ffmpeg = new FFmpeg(mpegPath);
        executor = new FFmpegExecutor(ffmpeg, ffprobe);
    }

    public VideoInfo processVideo(File in){
        var duration = getDuration(in.getPath());
        var coverTime = getARandomTimeForCover(duration);
        var size = getSize(in.getPath());
        var cover = getCover(in.getPath(), coverTime);
        return VideoInfo
                .builder()
                .fileSize(size)
                .cover(cover)
                .coverTime(coverTime)
                .duration(duration)
                .build();

    }

    private int getARandomTimeForCover(double duration) {
        return (int)(Math.random() * duration);
    }

    public MediaSizesBytes resizeVideos(File in, String ex){
        return MediaSizesBytes
                .builder()
                .huge(resize(in.getPath(), "wxga", ex))
                .large(resize(in.getPath(), "pal", ex))
                .medium(resize(in.getPath(), "sntsc", ex))
                .small(resize(in.getPath(), "qntsc", ex))
                .thumb(resize(in.getPath(), "qvga", ex))
                .mini(resize(in.getPath(), "qqvga", ex))
                .build();
    }

    private byte[] getCover(String in, double coverTime) {
        var pathToOut = "/tmp/" + UUID.randomUUID() + "." + "jpg";
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(in)
                .addOutput(pathToOut)
                .addExtraArgs("-ss" , ""+(int)coverTime, "-frames:v", "1", "-q:v","2")
                .done();
        executor.createJob(builder).run();
        return getBytesAndDelete(pathToOut);
    }

    private byte[]  resize(String in, String size, String ex){
        var pathToOut = "/tmp/" + UUID.randomUUID() + ex;
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(in)
                .addOutput(pathToOut)
                .setVideoResolution(size)
                .done();
        executor.createJob(builder).run();
        return getBytesAndDelete(pathToOut);
    }
}
