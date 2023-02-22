package com.isikato.fileutil.detecors;

import com.isikato.fileutil.model.FileInfo;
import com.isikato.fileutil.model.IsikatoFileType;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class FileTypeDetector {

    public FileInfo detect(InputStream in, String name) {
        TikaConfig config = TikaConfig.getDefaultConfig();

        Metadata metadata = new Metadata();
        InputStream stream = TikaInputStream.get(in);
        metadata.add(TikaCoreProperties.RESOURCE_NAME_KEY, name);
        try {
            var mediaType = config.getMimeRepository().detect(stream, metadata);
            var mimeType = config.getMimeRepository().forName(mediaType.toString());
            var extension = getExtension(name);
            var type = getType(mimeType.getName());
            return FileInfo
                    .builder()
                    .extension(extension)
                    .mime(mimeType.getName())
                    .name(extractName(name))
                    .type(type)
                    .build();
        } catch (IOException | MimeTypeException e) {
            throw new RuntimeException(e);
        }
    }

    private String getExtension(String name) {
        var splits = name.split("\\.");
        return "."+splits[splits.length - 1];
    }

    private String extractName(String name){
        var ex = getExtension(name);
        return name.substring(0, name.length() - ex.length())
                .replace(".", "-")
                .replace(" ", "-")
                .replace("_", "-");
    }

    private IsikatoFileType getType(String mime){
        if(mime.startsWith("image"))
            return IsikatoFileType.IMAGE;
        else if(mime.startsWith("video"))
            return IsikatoFileType.VIDEO;
        else if(mime.startsWith("audio"))
            return IsikatoFileType.AUDIO;
        else if(mime.startsWith("application/pdf") || mime.startsWith("application/msword"))
            return IsikatoFileType.BOOK;
        return IsikatoFileType.APP;
    }
}
