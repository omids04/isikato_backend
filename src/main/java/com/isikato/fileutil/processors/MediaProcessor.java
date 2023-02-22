package com.isikato.fileutil.processors;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.UUID;

@Slf4j
public abstract class MediaProcessor {

    public File toFile(InputStream is, String ex){
        try {
            var tempFile = File.createTempFile(UUID.randomUUID().toString(), ex);
            tempFile.deleteOnExit();
            copyInputStreamToFile(is, tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[8192];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }

    }

    protected byte[] getBytesAndDelete(String pathToOut) {
        var out = new File(pathToOut);
        try (var fis = new FileInputStream(out)){
            var outBytes = fis.readAllBytes();
            var wasDeleted = out.delete();
            if(!wasDeleted)
                log.warn("file with path {} could not be deleted. delete it manually", pathToOut);
            return outBytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
