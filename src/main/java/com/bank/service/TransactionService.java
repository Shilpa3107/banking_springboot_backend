package com.bank.service;
 
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.InsufficientBalanceException;
import com.bank.exception.InvalidAmountException;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.util.FileReportUtil;

import org.springframework.beans.factory.annotation.Value;

@Service
public class TransactionService {
    
    @Value("${banking.alert.threshold:1000}")
    private double threshold;

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final MailService mailService;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              MailService mailService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.mailService = mailService;
    }

    // ---------------------------
    // Deposit
    // ---------------------------
    @Transactional
    public Transaction deposit(String accountNumber, Double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount should not be negative or zero");
        }

        Account acc = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        acc.setBalance(acc.getBalance() + amount);
        accountRepository.save(acc);
        
        // Log to file like the original backend
        FileReportUtil.writeLine("DEPOSITE | Acc: " + accountNumber + " | Amount: " + amount);
        
        // Immediate check for alerts (or reset them if balance is now okay)
        if (acc.getBalance() >= threshold) {
            acc.setAlertCount(0);
            acc.setLastAlertAt(null);
            accountRepository.save(acc);
        }

        Transaction tx = new Transaction(acc, "DEPOSIT", amount, "Amount deposited");
        return transactionRepository.save(tx);
    }

    // ---------------------------
    // Withdraw
    // ---------------------------
    @Transactional
    public Transaction withdraw(String accountNumber, Double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount should not be negative or zero");
        }

        Account acc = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        if (acc.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient Balance");
        }

        acc.setBalance(acc.getBalance() - amount);
        accountRepository.save(acc);

        // Log to file
        FileReportUtil.writeLine("WITHDRAW | Acc: " + accountNumber + " | Amount: " + amount);
        
        // Immediate alert if balance falls below threshold
        checkAndSendImmediateAlert(acc);

        Transaction tx = new Transaction(acc, "WITHDRAW", amount, "Amount withdrawn");
        return transactionRepository.save(tx);
    }

    // ---------------------------
    // Transfer
    // ---------------------------
    @Transactional
    public String transfer(String fromAccountNumber, String toAccountNumber, Double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount should not be negative or zero");
        }

        Account from = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found: " + fromAccountNumber));

        Account to = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Receiver account not found: " + toAccountNumber));

        if (from.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient Balance");
        }

        from.setBalance(from.getBalance() - amount);
        accountRepository.save(from);

        to.setBalance(to.getBalance() + amount);
        accountRepository.save(to);
        
        // Log to file
        FileReportUtil.writeLine("TRANSFER | FromAcc: " + fromAccountNumber + " | ToAccount: " + toAccountNumber + " | Amount " + amount);
        
        // If receiver balance is now above threshold -> reset alerts
        if (to.getBalance() >= threshold) {
            to.setAlertCount(0);
            to.setLastAlertAt(null);
            accountRepository.save(to);
        }

        // Immediate alert for sender if needed
        checkAndSendImmediateAlert(from);

        Transaction tx1 = new Transaction(from, "TRANSFER", amount, "Sent to: " + toAccountNumber);
        Transaction tx2 = new Transaction(to, "TRANSFER", amount, "Received from: " + fromAccountNumber);
        transactionRepository.save(tx1);
        transactionRepository.save(tx2);

        return "Transfer successful";
    }

    private void checkAndSendImmediateAlert(Account acc) {
        if (acc.getBalance() < threshold) {
            String subject = "Low Balance Alert: " + acc.getAccountNumber();
            String message = "Dear " + acc.getHolderName() + ",\n\nYour account balance is Low: " + acc.getBalance() +
                    "\nPlease maintain minimum balance.";
            mailService.sendEmail(acc.getEmail(), subject, message);
        }
    }

    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }
}
