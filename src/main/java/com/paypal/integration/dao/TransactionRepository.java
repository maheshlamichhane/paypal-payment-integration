package com.paypal.integration.dao;

import com.paypal.integration.entity.PaypalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<PaypalTransaction,Long> {
}
