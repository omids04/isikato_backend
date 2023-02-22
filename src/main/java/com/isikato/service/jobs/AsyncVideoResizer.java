package com.isikato.service.jobs;

import com.isikato.fileutil.processors.VideoProcessor;
import com.isikato.infrastructure.repositories.FileDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncVideoResizer {

    private final VideoProcessor videoProcessor;
    private final FileDataRepository dataRepository;

    @Async
    public void resizeAndSave(List<Long> ids, File file, String ex){
        var sizes = videoProcessor.resizeVideos(file, ex);
        ids.forEach(id -> {
            var data = dataRepository.findById(id);
            data.ifPresent(fileData -> {
                switch (fileData.getType()){
                    case THUMB -> fileData.setData(sizes.getThumb());
                    case MINI -> fileData.setData(sizes.getMini());
                    case SMALL -> fileData.setData(sizes.getSmall());
                    case MEDIUM -> fileData.setData(sizes.getMedium());
                    case LARGE -> fileData.setData(sizes.getLarge());
                    case HUGE -> fileData.setData(sizes.getHuge());
                }
                dataRepository.save(fileData);
            });
        });
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            log.warn("Could not delete file with path {}. Please delete it manually", file.getPath());
        }
    }

}
