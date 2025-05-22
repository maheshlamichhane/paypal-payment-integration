package com.paypal.integration.security.config;

import com.paypal.integration.security.entity.Permission;
import com.paypal.integration.security.entity.Role;
import com.paypal.integration.security.entity.User;
import com.paypal.integration.security.service.UserServiceImpl;
import com.paypal.integration.security.util.JwtUtil;
import com.paypal.integration.util.Constants;
import com.paypal.integration.util.Utilities;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {


    private final JwtUtil jwtUtil;
    private final UserServiceImpl userService;
    private final UserServiceImpl userServiceImpl;
    private final Utilities utilities;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(Constants.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(Constants.BEARER)) {
            String jwt = authHeader.substring(7);
            if (jwtUtil.validateToken(jwt)) {
                String username = jwtUtil.extractUsername(jwt);
                User user = userServiceImpl.findByUsername(username);
                Set<SimpleGrantedAuthority> authorities = utilities.extractUserAuthorities(user);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, user.getPassword(), authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
