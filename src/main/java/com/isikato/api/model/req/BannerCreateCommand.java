package com.isikato.api.model.req;

import com.isikato.infrastructure.entities.Banner;
import com.isikato.infrastructure.entities.IsikatoFile;

public record BannerCreateCommand(long id, String title, String type, IdCommand image) {
    public Banner toDto(){
        long imgId = image == null ? 0L :image.id();
        return Banner
                .builder()
                .id(id)
                .title(title)
                .page(type)
                .image(IsikatoFile.builder().id(imgId).build())
                .build();
    }
}
