package com.bank.service;
 

import java.util.List;

import org.springframework.stereotype.Service;

import com.bank.entity.Account;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.InvalidAmountException;
import com.bank.repository.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // ---------------------------
    // Create Account (Signup)
    // ---------------------------
    public Account createAccount(String holderName, String email, Double initialBalance, String password) {
        if (initialBalance < 0) {
            throw new InvalidAmountException("Opening balance cannot be negative");
        }
        
        Account account = new Account(holderName, email, initialBalance, password);
        // Initially set a placeholder, then update with correct number based on ID
        account.setAccountNumber("PENDING");
        account = accountRepository.save(account);
        
        account.setAccountNumber(String.valueOf(1000000 + account.getId()));
        return accountRepository.save(account);
    }

    // ---------------------------
    // Login
    // ---------------------------
    public Account login(String identifier, String password) {
        // Try email first, then account number
        Account account = accountRepository.findByEmail(identifier)
                .orElseGet(() -> accountRepository.findByAccountNumber(identifier)
                .orElseThrow(() -> new AccountNotFoundException("Account not found")));

        if (!account.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }
        return account;
    }

    // ---------------------------
    // Get Account By ID
    // ---------------------------
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
    }
    
    // ---------------------------
    // Get Account By Number
    // ---------------------------
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with number: " + accountNumber));
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
