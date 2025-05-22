package com.paypal.integration.util;

import com.paypal.integration.security.entity.Permission;
import com.paypal.integration.security.entity.Role;
import com.paypal.integration.security.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class Utilities {


    public Set<SimpleGrantedAuthority>  extractUserAuthorities(User user){
        Set<Role> roles = user.getRoles();
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for(Role role : roles){
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            Set<Permission> permissions = role.getPermissions();
            for(Permission permission : permissions){
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }
        return authorities;
    }
}
