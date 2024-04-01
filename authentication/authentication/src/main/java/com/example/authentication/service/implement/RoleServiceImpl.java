package com.example.authentication.service.implement;

import com.example.authentication.entity.RoleEntity;
import com.example.authentication.model.Roles;
import com.example.authentication.repository.RoleRepository;
import com.example.authentication.service.interfaces.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private HashMap<String, Object> roleMap(RoleEntity roleEntity) {
        return new HashMap<>() {{
            put("roleName", roleEntity.getRoleName());
            put("roleDescription", roleEntity.getRoleDescription());
            put("permissions", roleEntity.getPermissions());
        }};
    }
    @Override
    public Roles createRole(Roles role) throws Exception {
        try {
            RoleEntity roleEntity = new RoleEntity();
            // Copy all the properties into role Entity and save to DB
            BeanUtils.copyProperties(role, roleEntity);
            roleRepository.save(roleEntity);
            return role;
        } catch(Exception e) {
            throw new Exception("Couldn't create Role" + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllRoles() {
        List<RoleEntity> roleEntities = roleRepository.findAll();
        List<Map<String, Object>> roleMapList = new ArrayList<>();
        roleEntities.forEach((roleEntity
                -> roleMapList.add(roleMap(roleEntity))));
        return roleMapList;
    }

    @Override
    public Map<String, Object> getRoleByName(String roleName) throws Exception {
        try {
            RoleEntity roleEntity = roleRepository.findByRoleName(roleName).isPresent()
                    ? roleRepository.findByRoleName(roleName).get() : null;
            assert roleEntity != null;
            return roleMap(roleEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get Role" + roleName + e.getMessage());
        }
    }

    @Override
    public Roles updateRole(Long roleId, Roles role) throws Exception {
        try {
            RoleEntity roleEntity = roleRepository.findById(roleId).isPresent() ? roleRepository.findById(roleId).get() : null;
            assert roleEntity != null;
            roleEntity.setRoleName(role.getRoleName());
            roleEntity.setRoleDescription(role.getRoleDescription());
            roleEntity.setUpdateAt(LocalDateTime.now());
            roleRepository.save(roleEntity);
            BeanUtils.copyProperties(roleEntity, role);
            return role;
        } catch (NoSuchElementException e) {
            throw new Exception("Role is not found" + e.getMessage());
        }
    }

    @Override
    public Boolean deleteRole(Long roleId) throws Exception {
        try {
            if (roleRepository.findById(roleId).isPresent()) {
                roleRepository.delete(roleRepository.findById(roleId).get());
                return true;
            }
            return false;
        } catch (NoSuchElementException e) {
            throw new Exception("Couldn't found role" + e.getMessage());
        }
    }
}
