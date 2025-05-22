package com.paypal.integration.util;

public class Constants {

    public static final String ACCESS_TOKEN_URL = "/v1/oauth2/token";
    public static final String CREATE_PAYMENT_URL = "/v2/checkout/orders";
    public static final String EXECUTE_PAYMENT_URL = "/v2/checkout/orders";
    public static final String PAYMENT_STATE_URL = "/v2/checkout/orders";

    public static final String GRANT_TYPE = "grant_type";
    public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";


    public static final String PAYMENT_FAILED = "Payment failed";
    public static final String PAYMENT_SUCCESS = "Payment success";
    public static final String PAYMENT_CANCELLED = "Payment cancelled";

    public static final String CAPTURE = "capture";
    public static final String AUTHORIZE = "authorize";

    public static final String INTENT = "intent";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String ID = "id";
    public static final String STATUS = "status";
    public static final String PURCHASE_UNITS = "purchase_units";
    public static final String AMOUNT = "amount";
    public static final String VALUE = "value";
    public static final String PAYER =  "payer";
    public static final String EMAIL_ADDRESS = "email_address";
    public static final String PAYEE = "payee";
    public static final String CREATE_TIME = "create_time";
    public static final String BASIC = "Basic ";

    public static final String LOGIN_URL = "/login";
    public static final String EXECUTE_URL = "/return";
    public static final String CANCEL_URL = "/cancel";


    public static final String INVALID_CREDENTIALS = "Please enter the correct email and password";
    public static final String SECRET_KEY = "mysupersecurekeythatis32byteslong!!";

    public static final String ADMIN = "ADMIN";
    public static final String CUSTOMER = "CUSTOMER";

}
