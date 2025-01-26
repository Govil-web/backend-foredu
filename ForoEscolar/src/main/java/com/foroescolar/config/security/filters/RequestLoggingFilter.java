package com.foroescolar.config.security.filters;

import com.foroescolar.exceptions.security.filters.FilterErrorHandler;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
@Slf4j
public class RequestLoggingFilter extends BaseSecurityFilter {

    public RequestLoggingFilter(FilterErrorHandler errorHandler) {
        super(errorHandler);
    }

    @Override
    protected void doFilterInternal(SecurityFilterContext context) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        log.info("Iniciando request: {} {} - RequestId: {}",
                context.request().getMethod(),
                context.request().getRequestURI(),
                requestId);

        context.filterChain().doFilter(context.request(), context.response());

        MDC.clear();
    }
}