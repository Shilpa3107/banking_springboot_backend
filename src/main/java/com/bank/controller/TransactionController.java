package com.bank.controller;

 
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.entity.Transaction;
import com.bank.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin("*") // allow React frontend
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // ---------------------------
    // Deposit
    // POST: /api/transactions/deposit
    // ---------------------------
    @PostMapping("/deposit")
    public Transaction deposit(@RequestParam Long accountId,
                               @RequestParam Double amount) {
        return transactionService.deposit(accountId, amount);
    }

    // ---------------------------
    // Withdraw
    // POST: /api/transactions/withdraw
    // ---------------------------
    @PostMapping("/withdraw")
    public Transaction withdraw(@RequestParam Long accountId,
                                @RequestParam Double amount) {
        return transactionService.withdraw(accountId, amount);
    }

    // ---------------------------
    // Transfer
    // POST: /api/transactions/transfer
    // ---------------------------
    @PostMapping("/transfer")
    public String transfer(@RequestParam Long fromAccountId,
                           @RequestParam Long toAccountId,
                           @RequestParam Double amount) {
        return transactionService.transfer(fromAccountId, toAccountId, amount);
    }

    // ---------------------------
    // Transaction History
    // GET: /api/transactions/history/{accountId}
    // ---------------------------
    @GetMapping("/history/{accountId}")
    public List<Transaction> getHistory(@PathVariable Long accountId) {
        return transactionService.getTransactionsByAccount(accountId);
    }
}
