package com.foroescolar.config.security.filters;

import com.foroescolar.exceptions.security.filters.FilterErrorHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public abstract class BaseSecurityFilter extends OncePerRequestFilter {
    private final FilterErrorHandler errorHandler;

    protected BaseSecurityFilter(FilterErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    protected FilterErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    protected void doFilterInternal (
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws  IOException {

        try {
            doFilterInternal(new SecurityFilterContext(request, response, filterChain));
        } catch (IllegalArgumentException | ServletException e) {
            errorHandler.handleException(e, response);
        }
    }

    protected abstract void doFilterInternal(SecurityFilterContext context) throws IllegalArgumentException, ServletException, IOException;
}