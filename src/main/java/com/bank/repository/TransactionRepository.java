package com.bank.repository;

 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Get all transactions of a specific account (latest first)
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);
}