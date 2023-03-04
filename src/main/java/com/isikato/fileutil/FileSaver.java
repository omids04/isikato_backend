package com.isikato.fileutil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class FileSaver {

    public static String saveVideo(String name, String size, String extension, byte[] data){
        var filename = name + "_" + size + LocalDateTime.now() + "." + extension;
        var path = "/var/isikato/files/videos/" + filename;
        try {
            Files.write(Path.of(path), data);
        } catch (IOException ignored) {
        }
        return path;
    }

    public static String saveImage(String name, String size, String extension, byte[] data){
        var filename = name + "_" + size + LocalDateTime.now() + "." + extension;
        var path = "/var/isikato/files/images/" + filename;
        try {
            Files.write(Path.of(path), data);
        } catch (IOException ignored) {
        }
        return path;
    }

    public static String saveBook(String name, String extension, byte[] data){
        var filename = name + "_"  + LocalDateTime.now() + "." + extension;
        var path = "/var/isikato/files/books/" + filename;
        try {
            Files.write(Path.of(path), data);
        } catch (IOException ignored) {
        }
        return path;
    }

    public static String saveApp(String name, String extension, byte[] data){
        var filename = name + "_"  + LocalDateTime.now() + "." + extension;
        var path = "/var/isikato/files/apps/" + filename;
        try {
            Files.write(Path.of(path), data);
        } catch (IOException ignored) {
        }
        return path;
    }

    public static String saveAudio(String name, String extension, byte[] data){
        var filename = name + "_"  + LocalDateTime.now() + "." + extension;
        var path = "/var/isikato/files/audios/" + filename;
        try {
            Files.write(Path.of(path), data);
        } catch (IOException ignored) {
        }
        return path;
    }
}
