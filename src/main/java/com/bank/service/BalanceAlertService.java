package com.bank.service;

 
 
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bank.entity.Account;
import com.bank.repository.AccountRepository;

@Service
public class BalanceAlertService {

    private final AccountRepository accountRepository;
    private final MailService mailService;

    // configurable threshold
    @Value("${banking.alert.threshold:1000}")
    private double threshold;

    public BalanceAlertService(AccountRepository accountRepository,
                               MailService mailService) {
        this.accountRepository = accountRepository;
        this.mailService = mailService;
    }

    // --------------------------------------------
    // Runs every 60 seconds to check low balances
    // --------------------------------------------
    @Scheduled(fixedDelay = 60000)  // still checks every 60 sec BUT sends email only after conditions match
    public void checkLowBalance() {

        List<Account> accounts = accountRepository.findAll();

        for (Account acc : accounts) {

            if (acc.getBalance() < threshold) {

                // If already sent 3 alerts, stop sending completely
            	int count = acc.getAlertCount();   
                if (count >= 3) {                 
                    continue;
                }

                Instant now = Instant.now();

                // If never sent before → send immediately
                if (acc.getLastAlertAt() == null) {
                    sendAlert(acc);
                    continue;
                }

                // Calculate 30 min (in seconds)
                long minutesSinceLast = java.time.Duration.between(acc.getLastAlertAt(), now).toMinutes();

                if (minutesSinceLast >= 1) {    // Send alert only after 30 min
                    sendAlert(acc);
                }
            }
        }
    }

    private void sendAlert(Account acc) {
        String subject = "Low Balance Alert";
        String msg = "Hi " + acc.getHolderName() +
                ",\n\nYour account balance is low.\n" +
                "Current balance: ₹" + acc.getBalance() +
                "\n\nPlease deposit soon.";

        mailService.sendEmail(acc.getEmail(), subject, msg);

        // update details
        acc.setLastAlertAt(Instant.now());
        acc.setAlertCount(acc.getAlertCount() + 1);

        accountRepository.save(acc);

        System.out.println("Alert sent to: " + acc.getEmail());
    }
}