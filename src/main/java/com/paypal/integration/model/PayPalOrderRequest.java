package com.paypal.integration.model;

import lombok.Data;

import java.util.List;

@Data
public class PayPalOrderRequest {
    private String intent;
    private List<PurchaseUnit> purchase_units;
    private PaymentSource payment_source;
}
