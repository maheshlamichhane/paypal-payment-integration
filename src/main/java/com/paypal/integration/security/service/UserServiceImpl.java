package com.paypal.integration.security.service;

import com.paypal.integration.security.dao.UserRepository;
import com.paypal.integration.security.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByUsername(String email) {
        return userRepository.findByEmail(email);
    }
}
