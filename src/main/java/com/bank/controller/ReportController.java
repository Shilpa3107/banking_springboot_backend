package com.bank.controller;
 
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.ZoneId;


import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.service.AccountService;
import com.bank.service.TransactionService;

@RestController
@RequestMapping("/reports")
@CrossOrigin("*")
public class ReportController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    public ReportController(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    // -----------------------------------------
    // Download transaction report as .txt file
    // GET: /reports/account/{accNo}/transactions
    // -----------------------------------------
    @GetMapping("/account/{accNo}/transactions")
    public ResponseEntity<ByteArrayResource> downloadReport(@PathVariable String accNo) {

        Account acc = accountService.getAccountByNumber(accNo);
        var transactions = transactionService.getTransactionsByAccount(acc.getId());

        // Format the report content
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        sb.append("Transaction Report for Account Number: ").append(accNo).append("\n");
        sb.append("Holder Name: ").append(acc.getHolderName()).append("\n");
        sb.append("--------------------------------------------------\n\n");

        for (Transaction tx : transactions) {

            LocalDateTime time = LocalDateTime.ofInstant(
                    tx.getCreatedAt(),
                    ZoneId.systemDefault()
            );

            sb.append("Date: ").append(fmt.format(time))
                    .append("\nType: ").append(tx.getType())
                    .append("\nAmount: ").append(tx.getAmount())
                    .append("\nDescription: ").append(tx.getDescription())
                    .append("\n--------------------------------------\n\n");
        }


        // Convert to byte resource
        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(bytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=transaction-report-account-" + accNo + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(bytes.length)
                .body(resource);
    }
}
