package com.paypal.integration.security.controller;

import com.paypal.integration.exception.AuthenticationException;
import com.paypal.integration.security.modal.LoginRequestDTO;
import com.paypal.integration.security.modal.LoginResponseDTO;
import com.paypal.integration.security.util.JwtUtil;
import com.paypal.integration.util.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LoginController {


    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> getAuthentication(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));
        } catch (Exception e) {
            throw new AuthenticationException(Constants.INVALID_CREDENTIALS,HttpStatus.BAD_REQUEST);
        }

        String token = jwtUtil.generateToken(loginRequestDTO.getUsername());
        LoginResponseDTO userDTO = LoginResponseDTO.builder()
                .username(loginRequestDTO.getUsername())
                .token(token)
                .expiration(3600000)
                .build();
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }
}
