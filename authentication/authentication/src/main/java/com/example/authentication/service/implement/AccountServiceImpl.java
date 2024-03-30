package com.example.authentication.service.implement;

import com.example.authentication.builder.AccountBuilder;
import com.example.authentication.builder.AuthenticationResponse;
import com.example.authentication.config.JwtService;
import com.example.authentication.entity.AccountEntity;
import com.example.authentication.entity.RoleEntity;
import com.example.authentication.entity.UserEntity;
import com.example.authentication.model.Accounts;
import com.example.authentication.repository.AccountBuilderRepository;
import com.example.authentication.repository.AccountRepository;
import com.example.authentication.repository.RoleRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.interfaces.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    private final AccountBuilderRepository accountBuilderRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    @Override
    public AuthenticationResponse authenticate(Accounts accounts) throws Exception {
        try{
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(accounts.getUserName(), accounts.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            AccountBuilder account = accountBuilderRepository.findByUserName(accounts.getUserName())
                    .orElseThrow(() -> new Exception("User is not found"));
            var jwtToken = jwtService.generateToken(account);
            return AuthenticationResponse.builder().token(jwtToken).build();
        }
        catch(NoSuchElementException e){
            throw new Exception(String.format("Account is existed %s", accounts.getUserName()));
        }
    }

    @Override
    public AuthenticationResponse createAccount(Accounts accounts) throws Exception {
       try{
        AccountEntity accountEntity = new AccountEntity();
        RoleEntity roleEntity;

        if(accountRepository.findByUserName(accounts.getUserName()).isPresent()){
            throw new Exception("User exists");
        }

        String encodedPassword = passwordEncoder.encode(accounts.getPassword());
        accounts.setPassword(encodedPassword);
        // Default Account will set Role to USER role when create new one
        if (accounts.getRoles() != null && accounts.getRoles().getRoleName() != null) {
            // If the repository already has Role -> use that role
            if (roleRepository.findByRoleName(accounts.getRoles().getRoleName()).isPresent()) {
                accounts.setRoles(roleRepository.findByRoleName(accounts.getRoles().getRoleName()).get());
            } else {
                // If the repository not have that role -> create one
                roleEntity = new RoleEntity(accounts.getRoles().getRoleName());
                roleRepository.save(roleEntity);
                accounts.setRoles(roleEntity);
            }
        } else {
            if(!roleRepository.findByRoleName(accountEntity.getRoles().getRoleName()).isPresent()) {
                // Create default role if didn't exists
                roleEntity = new RoleEntity(accountEntity.getRoles().getRoleName());
                roleRepository.save(roleEntity);
                accounts.setRoles(roleEntity);
            }
        }
        //Create new User when adding new account into database
        UserEntity userEntity = new UserEntity(accounts.getUserName());
        userRepository.save(userEntity);
        accounts.setUsers(userEntity);
        BeanUtils.copyProperties(accounts, accountEntity);
        accountRepository.save(accountEntity);
        var user = AccountBuilder.builder()
                    .userName(accounts.getUserName())
                    .password(encodedPassword)
                    .roles(accounts.getRoles())
                    .createAt(accounts.getCreateAt())
                    .updateAt(accounts.getUpdateAt())
                    .build();
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
       }catch(Exception e){
            throw new Exception(e.getMessage());
       }
    }

    @Override
    public boolean deleteAccount(Long id) throws Exception {
        try{
            if (accountRepository.findById(id).isPresent()) {
                accountRepository.delete(accountRepository.findById(id).get());
                return true;
            }
            return false;
        }catch(NoSuchElementException e){
            throw new Exception(String.format("Could not find any account within id: %s", id));
        }
    }

    @Override
    public Accounts getAccountsById(Long id) throws Exception {
        try{
            AccountEntity accountsEntity = accountRepository.findById(id).isPresent() ? accountRepository.findById(id).get() : null;
            Accounts account = new Accounts();
            assert accountsEntity != null;
            BeanUtils.copyProperties(accountsEntity, account);
            return account;
        }catch(NoSuchElementException e){
            throw new Exception(String.format("Could not find any account within id: %s", id));
        }
    }

    @Override
    public List<Map<String, Object>> getAllAccounts() {
        List<AccountEntity> accountsEntities = accountRepository.findAll();
        List<Map<String,Object>> accountMapList = new ArrayList<>();
        for (AccountEntity accountEntity : accountsEntities) {
            accountMapList.add(new HashMap<>(){{
                put("userName", accountEntity.getUserName());
                put("roleName", accountEntity.getRoles().getRoleName());
            }});
        }
        return accountMapList;
    }

    @Override
    public Long getAccIdByUserName (String userName) throws Exception {
        try {
            AccountEntity accountEntity = accountRepository.findByUserName(userName).isPresent() ? accountRepository.findByUserName(userName).get() : null;
            assert accountEntity != null;
            return accountEntity.getAcc_id();
        }
        catch (NoSuchElementException e){
            throw new Exception("User is not found");
        }
    }

    @Override
    public Accounts updatePasswordAccount(Long id, Accounts accounts) throws Exception {
       try{
            AccountEntity accountEntity = accountRepository.findById(id).isPresent() ? accountRepository.findById(id).get() : null;
            assert accountEntity != null;
            if(accounts.getPassword() == null){
                throw new Exception("Password editing must not be null");
            }
            else{
                accountEntity.setPassword(passwordEncoder.encode(accounts.getPassword()));
                accountEntity.setUpdateAt(LocalDateTime.now());
                accountRepository.save(accountEntity);
                return accounts;
            }
       } catch(NoSuchElementException e){
        throw new Exception(String.format("Could not find any account within id: %s", id));
       }
    }
    
}
