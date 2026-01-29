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
@RequestMapping("/transactions")
@CrossOrigin("*") 
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // ---------------------------
    // Deposit
    // POST: /transactions/deposite
    // ---------------------------
    @PostMapping("/deposite")
    public String deposit(@RequestBody TxRequest data) {
         transactionService.deposit(data.accNo, data.amount);
         return "Deposite successfully..!";
    }

    // ---------------------------
    // Withdraw
    // POST: /transactions/withdraw
    // ---------------------------
    @PostMapping("/withdraw")
    public String withdraw(@RequestBody TxRequest data) {
        transactionService.withdraw(data.accNo, data.amount);
        return "Withdraw successfully..!";
    }

    // ---------------------------
    // Transfer
    // POST: /transactions/transfer
    // ---------------------------
    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferRequest data) {
        return transactionService.transfer(data.fromAcc, data.toAcc, data.amount);
    }

    // DTOs for compatibility
    static class TxRequest {
        public String accNo;
        public Double amount;
    }

    static class TransferRequest {
        public String fromAcc;
        public String toAcc;
        public Double amount;
    }
}
