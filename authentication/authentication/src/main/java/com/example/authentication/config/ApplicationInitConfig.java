package com.example.authentication.config;

import com.example.authentication.entity.RoleEntity;
import com.example.authentication.model.Accounts;
import com.example.authentication.repository.AccountRepository;
import com.example.authentication.service.interfaces.AccountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    private final String DEFAULT_ADMIN = "admin";
    private final AccountRepository accountRepository;
    @Bean
    ApplicationRunner applicationRunner(AccountService accountService) {
        return args -> {
            if (accountRepository.findByUserName(DEFAULT_ADMIN).isPresent()) {
                log.warn("ADMIN user has been created before");
            } else {
                accountService.createAccount(new Accounts(DEFAULT_ADMIN, DEFAULT_ADMIN, new RoleEntity("ADMIN")));
                log.warn("ADMIN user has been created with default password: admin, please change it");
            }
        };
    }
}
