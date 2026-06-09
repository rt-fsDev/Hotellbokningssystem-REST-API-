package com.hotel.booking.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        // Step 1: Get Authorization header
        String authHeader = request.getHeader("Authorization");

        // Step 2: Check if header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Step 3: Extract token (remove "Bearer " prefix)
            String token = authHeader.substring(7);

            // Step 4: Validate token
            if (jwtUtil.validateToken(token)) {

                // Step 5: Extract username and role from token
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);

                // Step 6: Build UserDetails object
                UserDetails userDetails = User.withUsername(username)
                        .password("")
                        .authorities(new SimpleGrantedAuthority("ROLE_" + role))
                        .build();

                // Step 7: Create Authentication object
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Step 8: Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Step 9: Continue to next filter
        chain.doFilter(request, response);
    }
}