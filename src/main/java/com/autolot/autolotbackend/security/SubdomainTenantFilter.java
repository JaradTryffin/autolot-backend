package com.autolot.autolotbackend.security;

import com.autolot.autolotbackend.model.entity.Dealership;
import com.autolot.autolotbackend.repository.DealershipRepository;
import com.autolot.autolotbackend.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubdomainTenantFilter extends OncePerRequestFilter {

    private final DealershipRepository dealershipRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
            ) throws ServletException, IOException {

        try {
            String slug = getSubdomainSlug(request);

            // No subdomain (e.g. localhost:8080 or autolot.com) — skip tenant resolution
            // and let the request continue without a tenant context
            if (slug != null) {
                Optional<Dealership> dealership = dealershipRepository.findBySlug(slug);

                if (dealership.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Dealership not found");
                    return;
                }

                TenantContext.setDealershipId(dealership.get().getId());
            }

            filterChain.doFilter(request, response);
        } finally {
            // Always clear the TenantContext after the request completes.
            // ThreadLocal values persist on the thread, and servlet containers
            // reuse threads — without this, a subsequent request on the same thread
            // could see the previous request's dealership ID.
            TenantContext.clear();
        }
    }

    public String getSubdomainSlug(HttpServletRequest request) {
        String host = request.getHeader("Host");
        if (host == null) return null;

        String cleanHost = host.split(":")[0].toLowerCase();
        String[] parts = cleanHost.split("\\.");

        // Must have at least 3 parts (subdomain.domain.tld)
        // and the first part shouldn't be "www"
        if (parts.length >= 3 && !parts[0].equals("www")) {
            return parts[0];
        }

        return null; // Return null if it's localhost or a base domain
    }

}
