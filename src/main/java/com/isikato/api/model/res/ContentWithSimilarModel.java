package com.isikato.api.model.res;

import com.isikato.infrastructure.entities.Content;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;


@SuperBuilder
@Getter
public class ContentWithSimilarModel extends ContentDetailModel{

    private List<ContentDetailModel> similar;

public static ContentWithSimilarModel fromEntity(Content content, List<Content> sim){
        var similar = sim
                .stream()
                .map(ContentDetailModel::fromEntity)
                .collect(Collectors.toList());
        Writer writer = null;
        if(content.getWriter() != null)
        {
            if(content.getWriter().getImage() != null)
                writer = new Writer(content.getWriter().getUsername(), UploadFileResponse.forImage(content.getWriter().getImage()));
            else
                writer = new Writer(content.getWriter().getUsername(), null);
        }
        return ContentWithSimilarModel
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
                .cDate(content.getCreationTime())
                .uDate(content.getLastModifiedTime())
		        .counter(content.getVisitCounter())
                .images(getImages(content))
                .images2(getImages2(content))
                .files(toUploadModel(content.getFiles()))
                .categories(content.getCategories().stream().map(CategoryDetailsModel::fromDto).collect(Collectors.toList()))
                .title(content.getTitle())
                .similar(similar)
                .downloadCounter(content.getDownloadCounter())
                .published(content.isPublished())
                .writer(writer)
		.build();
    }

}

