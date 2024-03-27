package com.example.authentication.service.interfaces;

import com.example.authentication.builder.AuthenticationResponse;
import com.example.authentication.model.Accounts;
import java.util.*;

public interface AccountService {
    AuthenticationResponse createAccount(Accounts accounts) throws Exception;
    AuthenticationResponse authenticate(Accounts accounts) throws Exception;
    List<Map<String, Object>> getAllAccounts();
    boolean deleteAccount(Long id) throws Exception;
    Accounts getAccountsById(Long id) throws Exception;
    Long getAccIdByUserName (String userName) throws Exception;
    Accounts updatePasswordAccount(Long id, Accounts accounts) throws Exception;
}
