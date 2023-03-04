package com.isikato.service.jobs;

import com.isikato.fileutil.FileSaver;
import com.isikato.fileutil.processors.VideoProcessor;
import com.isikato.infrastructure.entities.FileData;
import com.isikato.infrastructure.repositories.FileDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
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
                var path = switch (fileData.getType()){
                    case THUMB -> FileSaver.saveImage(fileData.getFile().getName(), FileData.Type.THUMB.name(), ex, sizes.getThumb());
                    case MINI -> FileSaver.saveImage(fileData.getFile().getName(), FileData.Type.MINI.name(), ex, sizes.getMini());
                    case SMALL -> FileSaver.saveImage(fileData.getFile().getName(), FileData.Type.SMALL.name(), ex, sizes.getSmall());
                    case MEDIUM -> FileSaver.saveImage(fileData.getFile().getName(), FileData.Type.MEDIUM.name(), ex, sizes.getMedium());
                    case LARGE -> FileSaver.saveImage(fileData.getFile().getName(), FileData.Type.LARGE.name(), ex, sizes.getLarge());
                    case HUGE -> FileSaver.saveImage(fileData.getFile().getName(), FileData.Type.HUGE.name(), ex, sizes.getHuge());
                    case ORIGINAL, COVER -> null;
                };
                fileData.setPathToData(path);
                dataRepository.save(fileData);
            });
        });
    }

}
