package com.bank.service;
 

import java.util.List;

import org.springframework.stereotype.Service;

import com.bank.entity.Account;
import com.bank.repository.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // ---------------------------
    // Create Account
    // ---------------------------
    public Account createAccount(String holderName, String email, Double initialBalance) {
        Account account = new Account(holderName, email, initialBalance);
        return accountRepository.save(account);
    }

    // ---------------------------
    // Get Account By ID
    // ---------------------------
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    // ---------------------------
    // Get All Accounts
    // ---------------------------
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // ---------------------------
    // Update Account
    // ---------------------------
    public Account updateAccount(Long id, String holderName, String email) {
        Account acc = getAccountById(id);
        acc.setHolderName(holderName);
        acc.setEmail(email);
        return accountRepository.save(acc);
    }

    // ---------------------------
    // Delete Account
    // ---------------------------
    public void deleteAccount(Long id) {
        Account acc = getAccountById(id);
        accountRepository.delete(acc);
    }
}
