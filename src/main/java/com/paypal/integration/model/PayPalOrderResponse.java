package com.paypal.integration.model;

import lombok.Data;

import java.util.List;

@Data
public class PayPalOrderResponse {
    private String id;
    private String status;
    private PaymentSource payment_source;
    private Payer payer;
    private List<Link> links;
}

