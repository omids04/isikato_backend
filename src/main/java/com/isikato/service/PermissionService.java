package com.isikato.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.isikato.infrastructure.entities.Permission;
import com.isikato.infrastructure.repositories.PermissionRepository;
import com.isikato.service.dtos.CollectionWithCount;
import com.isikato.service.exceptions.PermissionNotFoundException;
import com.isikato.service.specs.PermissionSpecs;
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
public class PermissionService {

    private final PermissionRepository permissionRepository;


    public Permission createOrUpdate(Permission permission){

        var idNotZero = permission.getId() != 0;

        if (idNotZero && !checkIfPermissionExist(permission.getId()))
        {
            throw new PermissionNotFoundException(permission.getId());
        }

        return permissionRepository.save(permission);
    }


    public boolean remove(long id) {
        var deleteCount = permissionRepository.removeById(id);
        // deleteCount is either 0 or 1.
        return deleteCount == 1;
    }


    public Permission get(long id) {
        return this.getPermission(id);
    }


    public CollectionWithCount<Permission> getAll(PageRequest pageRequest, JsonNode filter){
        var page = permissionRepository.findAll(pageRequest);
        var count = page.getTotalElements();
        return new CollectionWithCount<>(page.toList(), count);
    }

    //************************** private methods ***************************

    private Permission getPermission(long id){
        var permission = permissionRepository.findById(id);
        if (permission.isEmpty())
            throw new RuntimeException(""+id);
        return permission.get();
    }

    private boolean checkIfPermissionExist(long id) {
        return permissionRepository.existsById(id);
    }
}
