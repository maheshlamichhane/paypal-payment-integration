package com.paypal.integration.model;

import lombok.Data;

@Data
public class PayPalAccessTokenResponse {
    private String scope;
    private String access_token;
    private String token_type;
    private String app_id;
    private String expires_in;
    private String nonce;
}
