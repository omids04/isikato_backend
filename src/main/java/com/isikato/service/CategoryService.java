package com.isikato.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.infrastructure.entities.Category;
import com.isikato.infrastructure.entities.IsikatoFile;
import com.isikato.infrastructure.repositories.CategoryRepository;
import com.isikato.infrastructure.repositories.FileRepository;
import com.isikato.service.dtos.CollectionWithCount;
import com.isikato.service.exceptions.CategoryNamingException;
import com.isikato.service.exceptions.CategoryNotFoundException;
import com.isikato.service.specs.CategorySpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 *
 * @author omid
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final FileRepository imageRepository;

    /**
     * Persists a Category if id is 0 Or Updates an exciting one if id is not 0.
     *
     * @throws CategoryNotFoundException if given id does not exist for update
     * @throws CategoryNamingException if given name for category already exists.
     * @param cat an object of type {@link Category}
     * @return an {@link Category} object that it's containing are in sync with database;
     */
    public Category createOrUpdate(Category cat)
            throws CategoryNotFoundException, CategoryNamingException {
        var idNotZero = cat.getId() != 0;

        // if id is 0 means it's a new cat that should get created
        // if not 0 we first should check if cat really exist
        if (idNotZero && !checkIfCatExist(cat.getId()))
        {
            throwForNonExistCat(cat.getId());
        }


        var catImg = this.getCatImage(cat);
        cat.setImage(catImg);

        try {
            return categoryRepository.save(cat);
        }catch (DataIntegrityViolationException ex){
            // only possible violation
            throw new CategoryNamingException(cat.getName());
        }
    }

    /**
     * removes a category from database
     * @param id of category
     * @return a boolean which is true if cat was successfully deleted.
     */
    public boolean remove(long id) {
        var deleteCount = categoryRepository.removeById(id);
        // deleteCount is either 0 or 1.
        return deleteCount == 1;
    }

    /**
     * @throws CategoryNotFoundException if category with given id does not exist.
     * @param id of wanted category
     * @return a single {@link Category} with given id
     */
    public Category get(long id) {
        return this.getCategory(id);
    }

    /**
     * @param skipLimit which contains pageable object
     * @param filter a {@link JsonNode} object which contains filters
     * @return a list of {@link Category}
     */
    public CollectionWithCount<Category> getAll(PageRequest pageReq, JsonNode filter){
        var page = categoryRepository.findAll(CategorySpecs.instance().fromFilter(filter), pageReq);
        var count = page.getTotalElements();
        return new CollectionWithCount<>(page.toList(), count);
    }


    //************************** private methods ***************************

    private Category getCategory(long id){
        var cat = categoryRepository.findById(id);
        if (cat.isEmpty())
            throwForNonExistCat(id);
        return cat.get();
    }

    private void throwForNonExistCat(long id){
        throw new CategoryNotFoundException(id);
    }

    private IsikatoFile getCatImage(Category cat){
        if(cat.getImage() == null)
            return null;
        var img = imageRepository.findById(cat.getImage().getId());
        return img.orElse(null);
    }

    private boolean checkIfCatExist(long id) {
        return categoryRepository.existsById(id);
    }
}
