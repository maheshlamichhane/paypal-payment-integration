package com.paypal.integration.security.service;


import com.paypal.integration.security.entity.User;

public interface UserService {

    public User findByUsername(String email);
}
