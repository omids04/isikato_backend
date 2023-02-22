package com.isikato.api.model.res;

import com.isikato.infrastructure.entities.Content;
import com.isikato.infrastructure.entities.IsikatoFile;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@SuperBuilder
public class ContentDetailModel {

    private long id;
    private String title;
    private String body;
    private String description;
    private String page;
    private boolean featured;

    private LocalDateTime cDate;
    private LocalDateTime uDate;

    private List<String> tags;
    private List<UploadFileResponse> files;
    private List<CategoryDetailsModel> categories;
    private List<UploadFileResponse> images;
    private List<UploadFileResponse> images2;

    private String extra1;
    private String extra2;
    private String extra3;
    private String extra4;
    private String extra5;

    private long counter;
    private long downloadCounter;

    private boolean published;
    private Writer writer;

    @Getter
    @AllArgsConstructor
    public static class Writer{
        private String name;
        private UploadFileResponse image;
    }

public static ContentDetailModel fromEntity(Content content){
        Writer writer = null;
        if(content.getWriter() != null)
        {
            if(content.getWriter().getImage() != null)
                writer = new Writer(content.getWriter().getUsername(), UploadFileResponse.forImage(content.getWriter().getImage()));
            else 
                writer = new Writer(content.getWriter().getUsername(), null);
        }
        return ContentDetailModel
                .builder()
                .id(content.getId())
                .body(content.getBody())
                .description(content.getDescription())
                .page(content.getPage())
                .tags(content.getTags())
                .extra1(content.getExtra1())
                .extra2(content.getExtra2())
                .extra3(content.getExtra3())
                .extra4(content.getExtra4())
                .extra5(content.getExtra5())
                .uDate(content.getLastModifiedTime())
                .cDate(content.getCreationTime())
                .featured(content.isFeatured())
                .files(toUploadModel(content.getFiles()))
                .categories(content.getCategories().stream().map(CategoryDetailsModel::fromDto).collect(Collectors.toList()))
                .images(getImages(content))
                .images2(getImages2(content))
                .title(content.getTitle())
                .counter(content.getVisitCounter())
                .published(content.isPublished())
                .downloadCounter(content.getDownloadCounter())
                .writer(writer)
		.build();
    }
    protected static List<UploadFileResponse> getImages(Content content) {
        var images = new ArrayList<UploadFileResponse>();
        for (var img : content.getImages()){
             images.add(UploadFileResponse.forImage(img));
        }
        return images;
    }

    protected static List<UploadFileResponse> getImages2(Content content) {
        var images = new ArrayList<UploadFileResponse>();
        for (var img : content.getImages2()){
            images.add(UploadFileResponse.forImage(img));
        }
        return images;
    }

    protected static List<UploadFileResponse> toUploadModel(List<IsikatoFile> files) {
        List<UploadFileResponse> l = new ArrayList<>();
        for (IsikatoFile file : files){
            if(file.getMime().startsWith("image"))
                 l.add(UploadFileResponse.forImage(file));
            else if(file.getMime().startsWith("video"))
                l.add(UploadFileResponse.forVideo(file));
            else if(file.getMime().startsWith("audio"))
                l.add(UploadFileResponse.forAudio(file));
            else
                l.add(UploadFileResponse.forBook(file));
        }
        return l;
    }
}
