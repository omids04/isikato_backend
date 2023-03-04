package com.isikato.api.model.res;

import com.isikato.infrastructure.entities.IsikatoFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;


public record UploadFileResponse(long id,
                                 String original,
                                 String mime,
                                 String extension,
                                 long size,
                                 BigDecimal duration,
                                 BigInteger coverTime,
                                 String cover,
                                 Map<String, String> sizes){

    private static String baseUrl = "api/files/";
    public static UploadFileResponse forImage(IsikatoFile img) {
        var sizes = new HashMap<String, String>();
        var id = img.getId();
        var name = img.getName();
        sizes.put("mini", baseUrl + id + "/" + "mini_" + name);
        sizes.put("thumb", baseUrl + id + "/" + "thumb_" + name);
        sizes.put("small", baseUrl + id + "/" + "small_" + name);
        sizes.put("medium", baseUrl + id + "/" + "medium_" + name);
        sizes.put("large", baseUrl + id + "/" + "large_" + name);

        var originalData = baseUrl + id + "/" + name;
        return new UploadFileResponse(img.getId(), originalData, img.getMime(),
                img.getExtension(),  img.getSize(),null, null, null, sizes);
    }

    public static UploadFileResponse forVideo(IsikatoFile video) {
        var sizes = new HashMap<String, String>();
        var id = video.getId();
        var name = video.getName();
        sizes.put("mini", baseUrl + id + "/" + "mini_" + name);
        sizes.put("thumb", baseUrl + id + "/" + "thumb_" + name);
        sizes.put("small", baseUrl + id + "/" + "small_" + name);
        sizes.put("medium", baseUrl + id + "/" + "medium_" + name);
        sizes.put("large", baseUrl + id + "/" + "large_" + name);
        sizes.put("huge", baseUrl + id + "/" + "huge_" + name);

        var originalData = baseUrl + id + "/" + name;
        var cover = baseUrl + id + "/" + "cover_" + name;
        return new UploadFileResponse(video.getId(), originalData, video.getMime(), video.getExtension(),
                video.getSize(),BigDecimal.valueOf(video.getDuration()), BigInteger.valueOf(video.getCoverTime()), cover, sizes);
    }

    public static UploadFileResponse forAudio(IsikatoFile audio) {
        var originalData = baseUrl + audio.getId() + "/" + audio.getName();
        return new UploadFileResponse(audio.getId(), originalData, audio.getMime(), audio.getExtension(),
                audio.getSize(),BigDecimal.valueOf(audio.getDuration()), null, null, null);
    }
    public static UploadFileResponse forApp(IsikatoFile app) {
        var originalData = baseUrl + app.getId() + "/" + app.getName();
        return new UploadFileResponse(app.getId(), originalData, app.getMime(), app.getExtension(),
                app.getSize(),null, null, null, null);
    }

    public static UploadFileResponse forBook(IsikatoFile book) {
        var originalData = baseUrl + book.getId() + "/" + book.getName();
        return new UploadFileResponse(book.getId(), originalData, book.getMime(), book.getExtension(),
                book.getSize(),null, null, null, null);
    }
}
