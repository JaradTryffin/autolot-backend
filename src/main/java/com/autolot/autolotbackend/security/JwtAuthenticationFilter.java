package com.autolot.autolotbackend.security;

import com.autolot.autolotbackend.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // If there's no header or it doesn't start with "Bearer ", skip this filter.
        // This allows public endpoints to pass through without any JWT processing.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token string (everything after "Bearer ")
        final String jwt = authHeader.substring(7);

        // Validate the token (checks signature, expiration, format).
        // If invalid, we don't authenticate — just let the request continue.
        // Spring Security will reject it later if the endpoint requires auth.
        if (!jwtService.validateJwtToken(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Token is valid — extract the claims (userId, dealershipId, email, role)
        Claims claims = jwtService.extractClaims(jwt);

        // Only set authentication if no one else has already authenticated this request
        if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Set the TenantContext so the rest of the app knows which dealership
            // this request belongs to. This is used by Hibernate filters to scope queries.
            TenantContext.setDealershipId(claims.get("dealershipId", String.class));

            // Create a Spring Security authentication token.
            // - principal (arg 1): the userId from the JWT subject — identifies WHO is making the request
            // - credentials (arg 2): null — we don't need a password, the JWT already proved identity
            // - authorities (arg 3): the user's role as a granted authority — used for @PreAuthorize, hasRole() etc.
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + claims.get("role", String.class)))
            );

            // Store the authentication in SecurityContext so Spring Security
            // knows this request is authenticated. Controllers can access the principal via
            // SecurityContextHolder.getContext().getAuthentication()
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Always continue the filter chain — pass the request to the next filter
        // and eventually to the controller. Without this, the request would hang.
        filterChain.doFilter(request, response);
    }
}
