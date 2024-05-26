package com.example.authentication.service.implement;

import com.example.authentication.entity.UserEntity;
import com.example.authentication.model.Users;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private Map<String, Object> userMap(UserEntity userEntity) {
        return new HashMap<>() {{
            put("id", userEntity.getUser_id());
            put("userName", userEntity.getUserName());
            put("adddress", userEntity.getAddress());
            put("birthDay", userEntity.getBirth_day());
            put("gender", userEntity.getGender());
        }};
    }

    @Override
    public Users createUsers(Users user) throws Exception{
        try{
            UserEntity userEntity = new UserEntity();
            user.setCreateAt(LocalDateTime.now());
            user.setUpdateAt(LocalDateTime.now());
            BeanUtils.copyProperties(user, userEntity);
            userRepository.save(userEntity);
            return user;
        }
        catch(Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public boolean deleteUser(Long id) throws Exception {
       try{
            if(userRepository.findById(id).isPresent()) {
               userRepository.delete(userRepository.findById(id).get());
               return true;
            }
            return false;
       }catch(NoSuchElementException e){
            throw new Exception("User is not found :" + id.toString());
       }
    }

    @Override
    public List<Map<String, Object>> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<Map<String, Object>> output = new ArrayList<>();
        userEntities.forEach((userEntity -> output.add(userMap(userEntity))));
        return output;
    }

    @Override
    public Map<String, Object> getUserById(Long id) throws Exception {
        try {
            UserEntity userEntity = userRepository.findById(id).isPresent() ? userRepository.findById(id).get() : null;
            // Assign all the properties USER Properties to users
            assert userEntity != null;
            return userMap(userEntity);
        }
        catch (NoSuchElementException e){
            throw new Exception("User is not found :" + id.toString());
        }
    }

    @Override
    public List<Map<String, Object>> getUserByName(String userName) throws Exception {
        try {
            List<UserEntity> userEntities = userRepository.findByUserNameContains(userName);
            List<Map<String, Object>> output = new ArrayList<>();
            userEntities.forEach((userEntity -> output.add(userMap(userEntity))));
            return output;
        } catch (NoSuchElementException e) {
            throw new Exception(String.format("Couldn't get user %s by name", userName));
        }
    }

    @Override
    public Map<String, Long> getUserIdByUserName(String userName) throws Exception {
        try {
            UserEntity userEntity = userRepository.findByUserName(userName).isPresent() ? userRepository.findByUserName(userName).get() : null;
            Map<String, Long> output = new Hashtable<>();
            assert userEntity != null;
            output.put("id", userEntity.getUser_id());
            return output;
        }
        catch (NoSuchElementException e){
            throw new Exception("User is not found :" + userName);
        }

    }

    @Override
    public Users updateUser(Long id, Users user) throws Exception {
        try{
            UserEntity userEntity = userRepository.findById(id).isPresent() ? userRepository.findById(id).get() : null;
            assert userEntity != null;
            userEntity.setAddress(user.getAddress());
            userEntity.setBirth_day(user.getBirth_day());
            userEntity.setGender(user.getGender());
            userEntity.setUpdateAt(LocalDateTime.now());
            userRepository.save(userEntity);
            BeanUtils.copyProperties(userEntity, user);
            return user;
        }
        catch (NoSuchElementException e){
            throw new Exception("User is not found :" + id.toString());
        }
    }
}
