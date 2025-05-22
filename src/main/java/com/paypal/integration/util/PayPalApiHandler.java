package com.paypal.integration.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.integration.exception.PaymentException;
import com.paypal.integration.model.*;
import com.paypal.integration.entity.PaypalTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
public class PayPalApiHandler {

    @Value("${paypal.url}")
    private String payPalUrl;

    @Value("${paypal.username}")
    private String payPalUsername;

    @Value("${paypal.password}")
    private String payPalPassword;

    @Value("${paypal.return.url}")
    private String returnUrl;

    @Value("${paypal.cancel.url}")
    private String cancelUrl;

    @Value("${paypal.webhook.id}")
    private String webhookId;

    private final  RestTemplate restTemplate;

    private final ObjectMapper objectMapper;



    public String getAccessToken() {

        // Create url
        String url = String.format("%s%s", payPalUrl, Constants.ACCESS_TOKEN_URL);

        // Create headers
        HttpHeaders headers = createAccessTokenHeaders();

        // Create body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(Constants.GRANT_TYPE, Constants.GRANT_TYPE_CLIENT_CREDENTIALS);

        // Create the request
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // Execute the request
        ResponseEntity<PayPalAccessTokenResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                PayPalAccessTokenResponse.class
        );
        ensureValidResponse(response,HttpStatus.OK,Constants.PAYMENT_FAILED);
        return response.getBody().getAccess_token();
    }



    public PayPalOrderResponse createPayment(PayPalOrderRequest payPalOrderRequest) {

        // Create url
        String url = String.format("%s%s", payPalUrl, Constants.CREATE_PAYMENT_URL);

        // Create headers
        HttpHeaders headers = createHeaders();

        // Create the body
        ExperienceContext context = payPalOrderRequest.getPayment_source().getPaypal().getExperience_context();
        context.setReturn_url(returnUrl);
        context.setCancel_url(cancelUrl);
        HttpEntity<PayPalOrderRequest> request = new HttpEntity<>(payPalOrderRequest, headers);


        // Execute the request
        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                PayPalOrderResponse.class
        );

        ensureValidResponse(response,HttpStatus.OK,Constants.PAYMENT_FAILED);
        return response.getBody();
    }


    @Retryable(
            value = { ResourceAccessException.class, ConnectException.class, SocketTimeoutException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public PaypalTransaction executePayment(String orderId,String type) throws JsonProcessingException {

        // Create url
        String url = String.format("%s%s/%s/%s",payPalUrl,Constants.EXECUTE_PAYMENT_URL,orderId,type);
        // Create headers
        HttpHeaders headers = createHeaders();
        // Create the request
        HttpEntity<Object> request = new HttpEntity<>(null, headers);
        // // Execute the request
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );
        ensureValidResponse(response,HttpStatus.CREATED,Constants.PAYMENT_FAILED);
        JsonNode root = parseJson(response.getBody());
        return createTransaction(root);
    }

    @Recover
    public String recoverGetAccessToken(Exception e) {
        throw new RuntimeException("Payment execution failed after retry");
    }

    public String getPaymentStateByOrderId(String tokenId) throws JsonProcessingException {
        String url = String.format("%s%s/%s",payPalUrl,Constants.PAYMENT_STATE_URL,tokenId);

        // Create headers
        HttpHeaders headers = createHeaders();

        // Create the request
        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        // Execute the request
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        ensureValidResponse(response,HttpStatus.OK,Constants.PAYMENT_FAILED);
        JsonNode root = parseJson(response.getBody());
        return root.path(Constants.INTENT).asText();
    }


    public boolean validateWebhookSignature(Map<String, String> headers, byte[] rawBody){
        HttpHeaders webhookHeaders = createHeaders();
        Map<String, Object> payload = createVerifySignaturePayload(headers,rawBody);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, webhookHeaders);
        ResponseEntity<Map> response = restTemplate.exchange(
                payPalUrl + "/v1/notifications/verify-webhook-signature",
                HttpMethod.POST,
                entity,
                Map.class
        );
        return "SUCCESS".equalsIgnoreCase((String) response.getBody().get("verification_status"));
    }


    private HttpHeaders createHeaders() {
        String accessToken = getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private JsonNode parseJson(String json) throws JsonProcessingException {
        return objectMapper.readTree(json);
    }

    private void ensureValidResponse(ResponseEntity<?> response, HttpStatus expectedStatus, String errorMsg) {
        if (!response.getStatusCode().equals(expectedStatus) || response.getBody() == null) {
            throw new PaymentException(errorMsg, HttpStatus.BAD_REQUEST);
        }
    }

    private PaypalTransaction createTransaction(JsonNode root){
        PaypalTransaction transaction = new PaypalTransaction();
        transaction.setOrderId(root.path(Constants.ID).asText());
        transaction.setStatus(root.path(Constants.STATUS).asText());
        transaction.setAmount(root.path(Constants.PURCHASE_UNITS)
                .get(0)
                .path(Constants.PAYMENTS)
                .path(Constants.AUTHORIZATIONS)
                .get(0)
                .path(Constants.AMOUNT)
                .path(Constants.VALUE)
                .asText());
        transaction.setBuyerId(root.path(Constants.PAYER)
                .path(Constants.PAYER_ID)
                .asText());
        transaction.setSellerEmail(root
                .path(Constants.PAYMENT_SOURCE)
                .path(Constants.PAYPAL)
                .path(Constants.EMAIL_ADDRESS)
                .asText());
        transaction.setCreatedAt(new Date().toString());
        return transaction;
    }

    private HttpHeaders createAccessTokenHeaders(){

        // Create basic auth
        String basicAuth = String.format("%s%s",Constants.BASIC,Base64.getEncoder()
                .encodeToString(String.format("%s:%s",payPalUsername,payPalPassword).getBytes(StandardCharsets.UTF_8)));

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set(Constants.AUTHORIZATION,basicAuth);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private Map<String, Object>  createVerifySignaturePayload(Map<String, String> headers,byte[] rawBody){

        Map<String, Object> payload = new HashMap<>();
        payload.put("transmission_id", headers.get("paypal-transmission-id"));
        payload.put("transmission_time", headers.get("paypal-transmission-time"));
        payload.put("cert_url", headers.get("paypal-cert-url"));
        payload.put("auth_algo", headers.get("paypal-auth-algo"));
        payload.put("transmission_sig", headers.get("paypal-transmission-sig"));
        payload.put("webhook_id", webhookId);

        Map<String, Object> webhookEvent;
        try {
            webhookEvent = objectMapper.readValue(rawBody, new TypeReference<Map<String, Object>>() {});
            payload.put("webhook_event", webhookEvent);
        } catch (IOException e) {
            throw new PaymentException("Invalid webhook event body",HttpStatus.BAD_REQUEST);
        }
        return payload;
    }

}


