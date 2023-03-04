package com.isikato.api.model.req;

import com.isikato.infrastructure.entities.Category;
import com.isikato.infrastructure.entities.Content;
import com.isikato.infrastructure.entities.IsikatoFile;

import java.util.List;
import java.util.stream.Collectors;

public record ContentCreateCommand(
        long id,
        List<IdCommand> files,
        List<IdCommand> images,
        List<IdCommand> images2,
        List<IdCommand> categories,
        boolean featured,
        String title,
        String body,
        String description,
        String page,
        String extra1,
        String extra2,
        String extra3,
        String extra4,
        String extra5,
        List<String> tags,
        boolean published) {
    public Content toEntity(){
        return Content
                .builder()
                .id(id)
                .tags(tags)
                .page(page)
                .featured(featured)
                .files(getFiles())
                .categories(getCategories())
                .images(getImages())
                .images2(getImages2())
                .body(body)
                .extra1(extra1)
                .extra2(extra2)
                .extra3(extra3)
                .extra4(extra4)
                .extra5(extra5)
                .description(description)
                .title(title)
                .published(published)
                .build();
    }

    private List<IsikatoFile> getFiles(){
        if(files == null)
            return List.of();
        return files
                .stream()
                .map(id -> IsikatoFile.builder().id(id.id()).build())
                .collect(Collectors.toList());
    }

    private List<Category> getCategories(){
        if(categories == null)
            return List.of();
        return categories
                .stream()
                .map(idModel -> Category.builder().id(idModel.id()).build())
                .collect(Collectors.toList());
    }

    private List<IsikatoFile> getImages(){
        if(images == null)
            return List.of();
        return images
                .stream()
                .map(idModel -> IsikatoFile.builder().id(idModel.id()).build())
                .collect(Collectors.toList());
    }

    private List<IsikatoFile> getImages2(){
        if(images2 == null)
            return List.of();
        return images2
                .stream()
                .map(idModel -> IsikatoFile.builder().id(idModel.id()).build())
                .collect(Collectors.toList());
    }
}
