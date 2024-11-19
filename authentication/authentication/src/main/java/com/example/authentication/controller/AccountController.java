package com.example.authentication.controller;

import com.example.authentication.builder.AuthenticationResponse;
import com.example.authentication.model.Accounts;
import com.example.authentication.service.interfaces.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    //Sign Up new account
    @PostMapping(value="/accounts/signup")
    public ResponseEntity<AuthenticationResponse> createAccount(@RequestBody Accounts account) throws Exception {
        return ResponseEntity.ok(accountService.createAccount(account));
    }
    //Sign In account
    @PostMapping(value="/accounts/signin")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody Accounts account) throws Exception{
        return ResponseEntity.ok(accountService.authenticate(account));
    }
    //Get all account
    @GetMapping(value = "/accounts")
    public ResponseEntity<List<Map<String, Object>>> getAllAccounts(){
        return ResponseEntity.ok(accountService.getAllAccounts());
    }
    //Get Account by ID
    @GetMapping(value = "/accounts/{accountId}")
    public ResponseEntity<Accounts> getAccountById(@PathVariable Long accountId) throws Exception {
        return ResponseEntity.ok(accountService.getAccountsById(accountId));
    }

    // Get account id by user name
    @GetMapping(value = "/accounts/find")
    public ResponseEntity<Long> getAccIdByUserName (@RequestParam("userName") String userName) throws Exception {
        return ResponseEntity.ok(accountService.getAccIdByUserName(userName));
    }

    // Check the account is Admin
    @GetMapping(value = "/accounts/admin")
    public ResponseEntity<Boolean> checkAdminAccount(@RequestParam("userName") String userName) throws Exception {
        return ResponseEntity.ok(accountService.checkAdminAccount(userName));
    }

    //Put Account Change Password
    @PutMapping(value = "/accounts/{id}")
    public ResponseEntity<Boolean> updateAccountPassword(@PathVariable Long id, @RequestBody String password) throws Exception{
        return ResponseEntity.ok(accountService.updatePasswordAccount(id, password));
    }
    //Delete Account
    @DeleteMapping(value = "/accounts/{userId}")
    public ResponseEntity<Map<String,Boolean>> deleteAccount(@PathVariable Long userId) throws Exception{
        return ResponseEntity.ok(new HashMap<>() {{
            put("deleted", accountService.deleteAccount(userId));
        }});
    }
}
