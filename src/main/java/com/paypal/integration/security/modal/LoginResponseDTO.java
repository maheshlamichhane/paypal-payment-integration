package com.paypal.integration.security.modal;

import lombok.*;

@Data
@Builder
public class LoginResponseDTO {

    private String token;

    private String username;

    private long expiration;

}
