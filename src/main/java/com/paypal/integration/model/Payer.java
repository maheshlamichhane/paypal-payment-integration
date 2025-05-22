package com.paypal.integration.model;

import lombok.Data;

@Data
public class Payer {
    private String email_address;
    private Address address;
}

