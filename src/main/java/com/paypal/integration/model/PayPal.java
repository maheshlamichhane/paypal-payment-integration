package com.paypal.integration.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayPal {
    private Address address;
    private String email_address;
    private String payment_method_preference;
    private ExperienceContext experience_context;
}

