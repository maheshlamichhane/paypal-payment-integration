package com.paypal.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paypal.integration.entity.PaypalTransaction;
import com.paypal.integration.exception.PaymentException;
import com.paypal.integration.model.PayPalOrderRequest;
import com.paypal.integration.model.PayPalOrderResponse;
import com.paypal.integration.service.PayPalPaymentService;
import com.paypal.integration.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PayPalPaymentController {

    private  final PayPalPaymentService payPalPaymentService;


    @PreAuthorize("((hasRole('CUSTOMER') and hasAuthority('CREATE_PAYMENT')) or (hasRole('ADMIN') and hasAuthority('CREATE_PAYMENT')))")
    @RequestMapping("/create")
    public void createPayPalOrder(@RequestBody PayPalOrderRequest payPalOrderRequest, HttpServletResponse response) throws IOException {
        PayPalOrderResponse payPalOrderResponse = payPalPaymentService.createPayPalOrder(payPalOrderRequest);
        String redirectUrl = payPalOrderResponse.getLinks().get(1).getHref();
        System.out.println("Redirect URL: " + redirectUrl);
        response.sendRedirect(redirectUrl);
    }


    @GetMapping("/return")
    @ResponseBody
    public ResponseEntity<String> handleReturn(@RequestParam("token") String token) throws JsonProcessingException {
        String intent = payPalPaymentService.findOrderTypeByOrderId(token);
        PaypalTransaction transaction;
        if(intent.equalsIgnoreCase(Constants.CAPTURE)){
            transaction = payPalPaymentService.executeTransaction(token,Constants.CAPTURE);
        }
        else if(intent.equalsIgnoreCase(Constants.AUTHORIZE)){
            transaction = payPalPaymentService.executeTransaction(token,Constants.AUTHORIZE);
        }
        else{
            throw new PaymentException(Constants.PAYMENT_FAILED, HttpStatus.BAD_REQUEST);
        }
        payPalPaymentService.savePayPalTransaction(transaction);
        return ResponseEntity.ok(Constants.PAYMENT_SUCCESS);
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> handleCancel() {
        return ResponseEntity.ok(Constants.PAYMENT_CANCELLED);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request) throws IOException {
        byte[] rawPayload = request.getInputStream().readAllBytes();
        Map<String, String> headers = Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        h -> h,
                        request::getHeader
                ));

        boolean isValid = payPalPaymentService.validateWebHookSignature(headers, rawPayload);

        if (isValid) {
            String body = new String(rawPayload, StandardCharsets.UTF_8);
            System.out.println("Valid webhook received:\n" + body);
        } else {
            System.out.println("Invalid signature.");
        }
        return ResponseEntity.ok().build();
    }
}
