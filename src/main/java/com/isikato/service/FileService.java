package com.isikato.service;

import com.isikato.fileutil.FileSaver;
import com.isikato.fileutil.detecors.FileTypeDetector;
import com.isikato.fileutil.model.IsikatoFileType;
import com.isikato.fileutil.processors.AudioProcessor;
import com.isikato.fileutil.processors.ImageProcessor;
import com.isikato.fileutil.processors.VideoProcessor;
import com.isikato.infrastructure.entities.FileData;
import com.isikato.infrastructure.entities.IsikatoFile;
import com.isikato.infrastructure.repositories.FileRepository;
import com.isikato.service.jobs.AsyncVideoResizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final ImageProcessor imageProcessor;
    private final FileRepository fileRepository;
    private final AsyncVideoResizer asyncVideoResizer;

    private final VideoProcessor videoProcessor;
    private final AudioProcessor audioProcessor;
    private final FileTypeDetector detector;

    public IsikatoFile handleFile(byte[] in, String name){
        var typeInfo = detector.detect(new ByteArrayInputStream(in), name);
        var isikatoFile = IsikatoFile
                .builder()
                .name(typeInfo.getName())
                .type(typeInfo.getType())
                .mime(typeInfo.getMime())
                .size(in.length)
                .fileData(new ArrayList<>(List.of()))
                .extension(typeInfo.getExtension())
                .build();
        return switch (typeInfo.getType()){
            case IMAGE -> handleImage(in , isikatoFile);
            case VIDEO -> handleVideo(in, isikatoFile);
            case AUDIO -> handleAudio(in, isikatoFile);
            case APP -> handleApp(in, isikatoFile);
            case BOOK -> handleBook(in, isikatoFile);
        };
    }

    private IsikatoFile handleImage(byte[] in, IsikatoFile isikatoFile) {

        var path = FileSaver.saveImage(isikatoFile.getName(), FileData.Type.ORIGINAL.name(), isikatoFile.getExtension(), in);
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.MINI).pathToData(path).build());

        var sizes = imageProcessor.resize(new File(path), isikatoFile.getExtension());



        path = FileSaver.saveImage(isikatoFile.getName(), FileData.Type.THUMB.name(), isikatoFile.getExtension(), sizes.getThumb());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.THUMB).pathToData(path).build());

        path = FileSaver.saveImage(isikatoFile.getName(), FileData.Type.MINI.name(), isikatoFile.getExtension(), sizes.getMini());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.MINI).pathToData(path).build());

        path = FileSaver.saveImage(isikatoFile.getName(), FileData.Type.SMALL.name(), isikatoFile.getExtension(), sizes.getSmall());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.SMALL).pathToData(path).build());

        path = FileSaver.saveImage(isikatoFile.getName(), FileData.Type.MEDIUM.name(), isikatoFile.getExtension(), sizes.getMedium());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.MEDIUM).pathToData(path).build());

        path = FileSaver.saveImage(isikatoFile.getName(), FileData.Type.LARGE.name(), isikatoFile.getExtension(), sizes.getLarge());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.LARGE).pathToData(path).build());

        path = FileSaver.saveImage(isikatoFile.getName(), FileData.Type.HUGE.name(), isikatoFile.getExtension(), sizes.getHuge());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.HUGE).pathToData(path).build());

        return fileRepository.save(isikatoFile);
    }
    private IsikatoFile handleVideo(byte[] in, IsikatoFile isikatoFile) {

        var path = FileSaver.saveVideo(isikatoFile.getName(), FileData.Type.ORIGINAL.name(), isikatoFile.getExtension(),in);
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.ORIGINAL).pathToData(path).build());

        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.THUMB).build());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.MINI).build());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.MEDIUM).build());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.MEDIUM).build());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.LARGE).build());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.HUGE).build());


        var otherInfo = videoProcessor.processVideo(new File(path));
        var coverPath = FileSaver.saveImage(isikatoFile.getName(), FileData.Type.COVER.name(), isikatoFile.getExtension(), otherInfo.getCover());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.COVER).pathToData(coverPath).build());

        isikatoFile.setCoverTime(otherInfo.getCoverTime());
        isikatoFile.setDuration(otherInfo.getDuration());
        isikatoFile.setType(IsikatoFileType.VIDEO);

        var persisted = fileRepository.save(isikatoFile);

        var ids = persisted.getFileData().stream().map(FileData::getId).collect(Collectors.toList());
        asyncVideoResizer.resizeAndSave(ids, new File(path), isikatoFile.getExtension());
        return persisted;
    }
    private IsikatoFile handleAudio(byte[] in, IsikatoFile isikatoFile) {

        var path = FileSaver.saveAudio(isikatoFile.getName(), isikatoFile.getExtension(), in);
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.ORIGINAL).pathToData(path).build());

        var otherInfo = audioProcessor.processAudio(new File(path));

        isikatoFile.setDuration(otherInfo.getDuration());
        isikatoFile.setType(IsikatoFileType.AUDIO);
        return fileRepository.save(isikatoFile);
    }
    private IsikatoFile handleBook(byte[] in, IsikatoFile isikatoFile) {
        isikatoFile.setType(IsikatoFileType.BOOK);

        var path = FileSaver.saveBook(isikatoFile.getName(), isikatoFile.getExtension(), in);
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.ORIGINAL).pathToData(path).build());

        return fileRepository.save(isikatoFile);
    }
    private IsikatoFile handleApp(byte[] in, IsikatoFile isikatoFile) {
        isikatoFile.setType(IsikatoFileType.BOOK);

        var path = FileSaver.saveApp(isikatoFile.getName(), isikatoFile.getExtension(), in);
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.ORIGINAL).pathToData(path).build());

        return fileRepository.save(isikatoFile);
    }

}
