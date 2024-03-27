package com.example.authentication.service.interfaces;
import com.example.authentication.model.Users;

import java.util.*;


public interface UserService {
    Users createUsers(Users user) throws Exception;
    List<Dictionary<String, String>> getAllUsers();
    Dictionary<String, String> getUserById(Long id) throws Exception;
    List<Dictionary<String, Object>> getUserByName (String userName) throws Exception;
    Dictionary<String, Long> getUserIdByUserName(String userName) throws Exception;
    Users updateUser(Long id, Users user) throws Exception;
    boolean deleteUser(Long id) throws Exception;
}
