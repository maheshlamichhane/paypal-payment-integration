package com.paypal.integration.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "paypal_transaction")
@Data
@NoArgsConstructor
public class PaypalTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name="order_id")
    private String orderId;

    @Column
    private String status;

    @Column
    private String amount;

    @Column(name = "buyer_id")
    private String buyerId;

    @Column(name = "seller_email")
    private String sellerEmail;

    @Column(name = "created_at")
    private String createdAt;
}

