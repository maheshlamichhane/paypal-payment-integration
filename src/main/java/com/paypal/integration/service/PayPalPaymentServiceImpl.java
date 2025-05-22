package com.paypal.integration.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paypal.integration.model.PayPalOrderRequest;
import com.paypal.integration.model.PayPalOrderResponse;
import com.paypal.integration.dao.TransactionRepository;
import com.paypal.integration.entity.PaypalTransaction;
import com.paypal.integration.util.PayPalApiHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PayPalPaymentServiceImpl implements PayPalPaymentService {

    private final PayPalApiHandler payPalApiHandler;

    private final TransactionRepository transactionRepository;


//  @CircuitBreaker(name = PAYPAL_CB, fallbackMethod = "createPayPalOrderFallback")
    @Override
    public PayPalOrderResponse createPayPalOrder(PayPalOrderRequest payPalOrderRequest) {
        return payPalApiHandler.createPayment(payPalOrderRequest);
    }

//    public void createPayPalOrderFallback(PayPalOrderRequest request, Throwable t) {
//        System.err.println("createPayPalOrder failed: " + t.getMessage());
//        throw new PaymentException( "PayPal service unavailable. Try again later.", HttpStatus.BAD_REQUEST);
//    }



    @Override
    public String findOrderTypeByOrderId(String orderId) throws JsonProcessingException {
        return payPalApiHandler.getPaymentStateByOrderId(orderId);
    }

    @Override
    public PaypalTransaction executeTransaction(String token,String type) throws JsonProcessingException {
        return payPalApiHandler.executePayment(token,type);
    }

    @Override
    public void savePayPalTransaction(PaypalTransaction paypalTransaction) {
        transactionRepository.save(paypalTransaction);
    }

    @Override
    public boolean validateWebHookSignature(Map<String, String> headers, byte[] rawBody) throws JsonProcessingException {
        return payPalApiHandler.validateWebhookSignature(headers, rawBody);
    }

}
