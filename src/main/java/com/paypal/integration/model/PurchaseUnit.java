package com.paypal.integration.model;

import lombok.Data;

@Data
public class PurchaseUnit {
    private String reference_id;
    private Amount amount;
}

