package com.isikato.api.model.res;

import com.isikato.infrastructure.entities.Banner;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BannerDetailsModel {

    private long id;
    private String title;
    private String type;
    private UploadFileResponse image;

    public static BannerDetailsModel fromDto(Banner banner){
        var img = banner.getImage() == null?
                null: UploadFileResponse.forImage(banner.getImage());
        return BannerDetailsModel
                .builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .type(banner.getPage())
                .image(img)
                .build();
    }
}
