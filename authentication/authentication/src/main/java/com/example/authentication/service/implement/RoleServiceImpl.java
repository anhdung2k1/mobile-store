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
    public List<Map<String, String>> getAllRoles() {
        List<RoleEntity> roleEntities = roleRepository.findAll();
        List<Map<String, String>> roleMapList = new ArrayList<>();
        for(RoleEntity roleEntity : roleEntities) {
            Map<String, String> role = new HashMap<>() {{
                put("roleName", roleEntity.getRoleName());
                put("roleDescription", roleEntity.getRoleDescription());
            }};
            roleMapList.add(role);
        }
        return roleMapList;
    }

    @Override
    public Map<String, String> getRoleByName(String roleName) throws Exception {
        try {
            RoleEntity roleEntity = roleRepository.findByRoleName(roleName).isPresent() ? roleRepository.findByRoleName(roleName).get() : null;
            assert roleEntity != null;
            return new HashMap<>(){{
               put("roleName", roleEntity.getRoleName());
               put("roleDescription", roleEntity.getRoleDescription());
            }};
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
