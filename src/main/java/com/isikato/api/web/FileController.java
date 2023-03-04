package com.isikato.api.web;
import com.isikato.api.model.res.*;

import com.isikato.infrastructure.entities.FileData;
import com.isikato.infrastructure.repositories.FileDataRepository;
import com.isikato.service.*;
import com.isikato.service.exceptions.FileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;
    private final FileDataRepository dataRepository;

    @PostMapping(value = "/uploadFile",
                    produces = MediaType.APPLICATION_JSON_VALUE )
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        var isikatoFile = fileService.handleFile(file.getBytes(), file.getOriginalFilename());
        return switch (isikatoFile.getType()){
            case VIDEO ->  UploadFileResponse.forVideo(isikatoFile);
            case IMAGE -> UploadFileResponse.forImage(isikatoFile);
            case AUDIO -> UploadFileResponse.forAudio(isikatoFile);
            case BOOK -> UploadFileResponse.forApp(isikatoFile);
            case APP -> UploadFileResponse.forBook(isikatoFile);
        };
    }

    @GetMapping(value = {"{id}/{name}"})
    public ResponseEntity<Resource> getFileData(@PathVariable("id") long id, @PathVariable(value = "name", required = false) String name) throws IOException {
        String sizeString = name.split("_")[0];
        FileData.Type type = FileData.Type.ORIGINAL;
        try {
            type = FileData.Type.valueOf(sizeString.toUpperCase());
        }catch (Exception ignored){
        }
        var data = dataRepository.findByTypeAndFileId(type, id);
        if(data.isEmpty())
            throw new FileNotFoundException(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(data.get().getFile().getMime()))
                .body(new ByteArrayResource(Files.readAllBytes(Path.of(data.get().getPathToData()))));
    }

}
