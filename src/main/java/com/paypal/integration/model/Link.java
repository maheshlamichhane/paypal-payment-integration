package com.paypal.integration.model;

import lombok.Data;

@Data
public class Link {
    private String href;
    private String rel;
    private String method;
}

