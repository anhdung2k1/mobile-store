package com.example.authentication.service.implement;

import com.example.authentication.entity.PermissionEntity;
import com.example.authentication.model.Permission;
import com.example.authentication.repository.PermissionRepository;
import com.example.authentication.service.interfaces.PermissionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    private HashMap<String, Object> permissionMap(PermissionEntity permissionEntity) {
        return new HashMap<>() {{
            put("permissionName", permissionEntity.getPermissionName());
            put("permissionModule", permissionEntity.getPermissionModule());
        }};
    }
    @Override
    public Permission createPermission(Permission permission) throws Exception {
        try {
            PermissionEntity permissionEntity = new PermissionEntity();
            // Copy all the properties into permission Entity and save to DB
            BeanUtils.copyProperties(permission, permissionEntity);
            permissionRepository.save(permissionEntity);
            return permission;
        } catch (Exception e) {
            throw new Exception("Could not create Permission" + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllPermission() {
        List<PermissionEntity> permissionEntities = permissionRepository.findAll();
        List<Map<String, Object>> perMapList = new ArrayList<>();
        permissionEntities.forEach((permissionEntity
                -> perMapList.add(permissionMap(permissionEntity))));
        return perMapList;
    }

    @Override
    public Map<String, Object> getPermissionByName(String permissionName) throws Exception {
        try {
            PermissionEntity permissionEntity = permissionRepository.findByPermissionName(permissionName).isPresent()
                    ? permissionRepository.findByPermissionName(permissionName).get() : null;
            assert permissionEntity != null;
            return permissionMap(permissionEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get Permission" + permissionName + e.getMessage());
        }
    }

    @Override
    public Permission updatePermission(Long permissionId, Permission permission) throws Exception {
        try {
            PermissionEntity permissionEntity = permissionRepository.findById(permissionId).isPresent() ?
                    permissionRepository.findById(permissionId).get() : null;
            assert permissionEntity != null;
            permissionEntity.setPermissionName(permission.getPermissionName());
            permissionEntity.setPermissionModule(permission.getPermissionModule());
            permissionEntity.setUpdateAt(LocalDateTime.now());
            permissionRepository.save(permissionEntity);
            BeanUtils.copyProperties(permissionEntity, permission);
            return permission;
        } catch (NoSuchElementException e) {
            throw new Exception("Role is not found" + e.getMessage());
        }
    }

    @Override
    public Boolean deletePermission(Long permissionId) throws Exception {
        try {
            if(permissionRepository.findById(permissionId).isPresent()) {
                permissionRepository.delete(permissionRepository.findById(permissionId).get());
                return true;
            }
            return false;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not found role" + e.getMessage());
        }
    }
}
