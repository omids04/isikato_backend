package com.isikato.fileutil.processors;

import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import java.io.IOException;

public abstract class ContinuousMediaProcessor extends MediaProcessor{

    protected final FFprobe ffprobe;

    public ContinuousMediaProcessor(String probePath) throws IOException {
        ffprobe = new FFprobe(probePath);
    }

    protected double getDuration(String in){
        try {
            FFmpegProbeResult probeResult = ffprobe.probe(in);
            FFmpegFormat format = probeResult.getFormat();
            return format.duration;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected long getSize(String in){
        try {
            FFmpegProbeResult probeResult = ffprobe.probe(in);
            FFmpegFormat format = probeResult.getFormat();
            return format.size;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
