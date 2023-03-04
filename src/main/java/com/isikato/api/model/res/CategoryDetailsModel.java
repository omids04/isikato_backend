package com.isikato.api.model.res;

import com.isikato.infrastructure.entities.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryDetailsModel {

    private long id;
    private String name;
    private String type;
    private String description;
    private UploadFileResponse image;

    public static CategoryDetailsModel fromDto(Category category){
        var img = category.getImage() == null?
                null: UploadFileResponse.forImage(category.getImage());
        return CategoryDetailsModel
                .builder()
                .id(category.getId())
                .description(category.getDescription())
                .type(category.getType())
                .name(category.getName())
                .image(img)
                .build();
    }
}
