package com.foroescolar.config.security.filters;

import com.foroescolar.exceptions.security.filters.token.TokenInvalidatedException;
import com.foroescolar.exceptions.security.filters.FilterErrorHandler;
import com.foroescolar.services.TokenService;
import com.foroescolar.utils.token.TokenExtractor;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@Order(3)
@Slf4j
public class BlacklistTokenFilter extends BaseSecurityFilter {
    private final TokenService tokenService;

    public BlacklistTokenFilter(
            FilterErrorHandler errorHandler,
            TokenService tokenService) {
        super(errorHandler);
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(SecurityFilterContext context) throws ServletException, IOException {
        Optional<String> token = TokenExtractor.extractFromRequest(context.request());
//
//        if (token.isPresent() && tokenService.isTokenInBlacklist(token.get())) {
//            throw new TokenInvalidatedException("Token ha sido invalidado");
//        }
        log.info("Token no está en la lista negra: {}", token);

        context.filterChain().doFilter(context.request(), context.response());
    }
}
