package com.paypal.integration.security.config;


import com.paypal.integration.security.entity.User;
import com.paypal.integration.security.modal.UserDTO;
import com.paypal.integration.security.service.UserServiceImpl;
import com.paypal.integration.util.Utilities;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserServiceImpl userServiceImpl;
    private final Utilities utilities;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userServiceImpl.findByUsername(email);
        Set<SimpleGrantedAuthority> authorities = utilities.extractUserAuthorities(user);
        return new UserDTO(user.getEmail(),user.getPassword(),authorities);
    }

}
