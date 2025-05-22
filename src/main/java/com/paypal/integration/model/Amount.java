package com.paypal.integration.model;

import lombok.Data;

@Data
public class Amount {
    private String currency_code;
    private String value;
}

