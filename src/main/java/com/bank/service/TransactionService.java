package com.bank.service;
 
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;

import org.springframework.beans.factory.annotation.Value;
@Service
public class TransactionService {
	
	@Value("${banking.alert.threshold}")
	private double threshold;


    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // ---------------------------
    // Deposit
    // ---------------------------
    @Transactional
    public Transaction deposit(Long accountId, Double amount) {
        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        acc.setBalance(acc.getBalance() + amount);
        accountRepository.save(acc);
        
        // ✅ If balance is now above threshold, reset alert counters
        if (acc.getBalance() >= 1000) {  // or use @Value property
            acc.setAlertCount(0);
            acc.setLastAlertAt(null);
        }
        
         

        Transaction tx = new Transaction(acc, "DEPOSIT", amount, "Amount deposited");
        return transactionRepository.save(tx);
    }

    // ---------------------------
    // Withdraw
    // ---------------------------
    @Transactional
    public Transaction withdraw(Long accountId, Double amount) {
        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (acc.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        acc.setBalance(acc.getBalance() - amount);
        accountRepository.save(acc);

        Transaction tx = new Transaction(acc, "WITHDRAW", amount, "Amount withdrawn");
        return transactionRepository.save(tx);
    }

    // ---------------------------
    // Transfer
    // ---------------------------
    @Transactional
    public String transfer(Long fromAccountId, Long toAccountId, Double amount) {
        Account from = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account to = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (from.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance for transfer");
        }

        // Deduct from sender
        from.setBalance(from.getBalance() - amount);
        accountRepository.save(from);

        // Add to receiver
        to.setBalance(to.getBalance() + amount);
        accountRepository.save(to);
        
     // If receiver balance is now above threshold → reset alerts
        if (to.getBalance() >= threshold) {
            to.setAlertCount(0);
            to.setLastAlertAt(null);
            accountRepository.save(to);
        }

        // Log transactions for both accounts
        Transaction tx1 = new Transaction(from, "TRANSFER", amount,
                "Sent to account ID: " + toAccountId);
        Transaction tx2 = new Transaction(to, "TRANSFER", amount,
                "Received from account ID: " + fromAccountId);

        transactionRepository.save(tx1);
        transactionRepository.save(tx2);

        return "Transfer successful";
    }

    // ---------------------------
    // Get Transaction History
    // ---------------------------
    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }
}
