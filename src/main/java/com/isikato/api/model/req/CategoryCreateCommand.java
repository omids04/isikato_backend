package com.isikato.api.model.req;

import com.isikato.infrastructure.entities.Category;
import com.isikato.infrastructure.entities.IsikatoFile;

public record CategoryCreateCommand(long id,
                                    String name,
                                    String type,
                                    String description,
                                    IdCommand image) {
    public Category toDto(){
        long imgId = image == null ? 0L :image.id();
        return Category
                .builder()
                .id(id)
                .name(name)
                .description(description)
                .type(type)
                .image(IsikatoFile.builder().id(imgId).build())
                .build();
    }
}
