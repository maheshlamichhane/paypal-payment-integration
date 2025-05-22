package com.paypal.integration.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.paypal.integration.model.PayPalOrderRequest;
import com.paypal.integration.model.PayPalOrderResponse;
import com.paypal.integration.entity.PaypalTransaction;

import java.util.Map;

public interface PayPalPaymentService {

    public PayPalOrderResponse createPayPalOrder(PayPalOrderRequest payPalOrderRequest);
    public String findOrderTypeByOrderId(String orderId) throws JsonProcessingException;
    public PaypalTransaction executeTransaction(String token,String type) throws JsonProcessingException;
    public void savePayPalTransaction(PaypalTransaction paypalTransaction);
    public boolean validateWebHookSignature(Map<String, String> headers, byte[] rawBody) throws JsonProcessingException;
}
