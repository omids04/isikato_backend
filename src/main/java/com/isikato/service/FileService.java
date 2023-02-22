package com.isikato.service;

import com.isikato.fileutil.detecors.FileTypeDetector;
import com.isikato.fileutil.model.IsikatoFileType;
import com.isikato.fileutil.processors.AudioProcessor;
import com.isikato.fileutil.processors.ImageProcessor;
import com.isikato.fileutil.processors.VideoProcessor;
import com.isikato.infrastructure.entities.FileData;
import com.isikato.infrastructure.entities.IsikatoFile;
import com.isikato.infrastructure.repositories.FileDataRepository;
import com.isikato.infrastructure.repositories.FileRepository;
import com.isikato.service.jobs.AsyncVideoResizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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
                .extension(typeInfo.getExtension())
                .build();
        var file = imageProcessor.toFile(new ByteArrayInputStream(in), typeInfo.getExtension());
        try {
            var data = Files.readAllBytes(file.toPath());
            isikatoFile.setSize(data.length);
            var fileData = FileData.builder().type(FileData.Type.ORIGINAL).file(isikatoFile).data(data).build();
            isikatoFile.setFileData(new ArrayList<>(List.of(fileData)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return switch (typeInfo.getType()){
            case IMAGE -> handleImage(file , isikatoFile);
            case VIDEO -> handleVideo(file, isikatoFile);
            case AUDIO -> handleAudio(file, isikatoFile);
            case APP -> handleApp(file, isikatoFile);
            case BOOK -> handleBook(file, isikatoFile);
        };
    }

    private IsikatoFile handleImage(File in, IsikatoFile isikatoFile) {
      var sizes = imageProcessor.resize(in, isikatoFile.getExtension());
      isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.THUMB).data(sizes.getThumb()).build());
      isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.MINI).data(sizes.getMini()).build());
      isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.SMALL).data(sizes.getSmall()).build());
      isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.MEDIUM).data(sizes.getMedium()).build());
      isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.LARGE).data(sizes.getLarge()).build());
      isikatoFile.setType(IsikatoFileType.IMAGE);
      var deleted = in.delete();
      if(!deleted) {
          log.warn("file with path {} could not be deleted. please delete it manually", in.getPath());
      }
      return fileRepository.save(isikatoFile);
    }
    private IsikatoFile handleVideo(File in, IsikatoFile isikatoFile) {
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.THUMB).build());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.MINI).build());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.MEDIUM).build());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.MEDIUM).build());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.LARGE).build());
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.HUGE).build());

        var otherInfo = videoProcessor.processVideo(in);
        isikatoFile.getFileData().add(FileData.builder().file(isikatoFile).type(FileData.Type.COVER).data(otherInfo.getCover()).build());

        isikatoFile.setCoverTime(otherInfo.getCoverTime());
        isikatoFile.setDuration(otherInfo.getDuration());
        isikatoFile.setType(IsikatoFileType.VIDEO);

        var persisted = fileRepository.save(isikatoFile);

        var ids = persisted.getFileData().stream().map(FileData::getId).collect(Collectors.toList());
        asyncVideoResizer.resizeAndSave(ids, in, isikatoFile.getExtension());
        return persisted;
    }
    private IsikatoFile handleAudio(File in, IsikatoFile file) {
        var otherInfo = audioProcessor.processAudio(in);

        file.setDuration(otherInfo.getDuration());
        file.setType(IsikatoFileType.AUDIO);
        return fileRepository.save(file);
    }
    private IsikatoFile handleBook(File in, IsikatoFile file) {
        file.setType(IsikatoFileType.BOOK);
        return fileRepository.save(file);
    }
    private IsikatoFile handleApp(File in, IsikatoFile file) {
        file.setType(IsikatoFileType.BOOK);
        return fileRepository.save(file);
    }

}
