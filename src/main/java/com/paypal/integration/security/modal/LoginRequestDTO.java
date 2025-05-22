package com.paypal.integration.security.modal;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;



@Data
public class LoginRequestDTO {

    @Email(message="Please enter a valid email.")
    @NotBlank(message ="Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
