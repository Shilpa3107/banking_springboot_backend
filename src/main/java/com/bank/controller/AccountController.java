package com.bank.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.entity.Account;
import com.bank.service.AccountService;

@RestController
@RequestMapping("/accounts")
@CrossOrigin("*") 
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // ---------------------------
    // Signup (Create Account)
    // POST: /accounts/create
    // ---------------------------
    @PostMapping("/create")
    public Account createAccount(@RequestBody AccountRequest data) {
        return accountService.createAccount(
                data.name,
                data.email,
                data.balance,
                data.password
        );
    }

    // ---------------------------
    // Login
    // POST: /accounts/login
    // ---------------------------
    @PostMapping("/login")
    public Account login(@RequestBody LoginRequest data) {
        return accountService.login(data.identifier, data.password);
    }

    // ---------------------------
    // Get Account By Number
    // GET: /accounts/{accNo}
    // ---------------------------
    @GetMapping("/{accNo}")
    public Account getAccount(@PathVariable String accNo) {
        return accountService.getAccountByNumber(accNo);
    }

    // ---------------------------
    // List All Accounts
    // GET: /accounts/all
    // ---------------------------
    @GetMapping("/all")
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    // DTO for compatibility
    static class AccountRequest {
        public String name;
        public String email;
        public Double balance;
        public String password;
    }

    static class LoginRequest {
        public String identifier;
        public String password;
    }
}
