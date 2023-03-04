package com.isikato.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.infrastructure.entities.Banner;
import com.isikato.infrastructure.entities.IsikatoFile;
import com.isikato.infrastructure.repositories.BannerRepository;
import com.isikato.infrastructure.repositories.FileRepository;
import com.isikato.service.dtos.CollectionWithCount;
import com.isikato.service.exceptions.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 *
 * @author omid
 */
@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;
    private final FileRepository imageRepository;


    public Banner createOrUpdate(Banner banner) {
        var idNotZero = banner.getId() != 0;

        if (idNotZero && !checkIfBannerExist(banner.getId()))
        {
            throwForNonExistBanner(banner.getId());
        }


        var bannerImg = this.getBannerImage(banner);
        banner.setImage(bannerImg);
        return bannerRepository.save(banner);
    }


    public boolean remove(long id) {
        var deleteCount = bannerRepository.removeById(id);
        // deleteCount is either 0 or 1.
        return deleteCount == 1;
    }


    public Banner get(long id) {
        return this.getBanner(id);
    }


    public CollectionWithCount<Banner> getAll(PageRequest pageReq, JsonNode filter){
        var page = bannerRepository.findAll(pageReq);
        var count = page.getTotalElements();
        return new CollectionWithCount<>(page.toList(), count);
    }


    //************************** private methods ***************************

    private Banner getBanner(long id){
        var banner = bannerRepository.findById(id);
        if (banner.isEmpty())
            throwForNonExistBanner(id);
        return banner.get();
    }

    private void throwForNonExistBanner(long id){
        throw new CategoryNotFoundException(id);
    }

    private IsikatoFile getBannerImage(Banner banner){
        if(banner.getImage() == null)
            return null;
        var img = imageRepository.findById(banner.getImage().getId());
        return img.orElse(null);
    }

    private boolean checkIfBannerExist(long id) {
        return bannerRepository.existsById(id);
    }
}
