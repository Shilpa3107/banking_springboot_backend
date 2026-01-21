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
@RequestMapping("/api/accounts")
@CrossOrigin("*")   // allow frontend to access (React)
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // ---------------------------
    // Create Account
    // POST: /api/accounts
    // ---------------------------
    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountService.createAccount(
                account.getHolderName(),
                account.getEmail(),
                account.getBalance()
        );
    }

    // ---------------------------
    // Get Account By ID
    // GET: /api/accounts/{id}
    // ---------------------------
    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    // ---------------------------
    // List All Accounts
    // GET: /api/accounts
    // ---------------------------
    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    // ---------------------------
    // Update Account
    // PUT: /api/accounts/{id}
    // ---------------------------
    @PutMapping("/{id}")
    public Account updateAccount(
            @PathVariable Long id,
            @RequestBody Account account) {

        return accountService.updateAccount(
                id,
                account.getHolderName(),
                account.getEmail()
        );
    }

    // ---------------------------
    // Delete Account
    // DELETE: /api/accounts/{id}
    // ---------------------------
    @DeleteMapping("/{id}")
    public String deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return "Account deleted successfully";
    }
}
