package com.bank.entity;
 

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "holder_name", nullable = false)
    private String holderName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Double balance = 0.0;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Account() {}

    public Account(String holderName, String email, Double balance, String password) {
        this.holderName = holderName;
        this.email = email;
        this.balance = balance == null ? 0.0 : balance;
        this.password = password;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        // accountNumber will be set by the service to ensure it's like the original (counter-based or id-based)
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    @Column(name = "last_alert_at")
    private Instant lastAlertAt;

    @Column(name = "alert_count")
    private Integer alertCount = 0;

    public Instant getLastAlertAt() { return lastAlertAt; }
    public void setLastAlertAt(Instant lastAlertAt) { this.lastAlertAt = lastAlertAt; }

    public Integer getAlertCount() {
        if (alertCount == null) alertCount = 0;
        return alertCount;
    }

    public void setAlertCount(Integer alertCount) {
        this.alertCount = (alertCount == null ? 0 : alertCount);
    }
}
