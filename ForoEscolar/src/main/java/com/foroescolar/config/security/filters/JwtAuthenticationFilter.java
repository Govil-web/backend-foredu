package com.foroescolar.config.security.filters;

import com.foroescolar.exceptions.security.filters.FilterErrorHandler;
import com.foroescolar.exceptions.security.filters.token.JwtAuthenticationException;
import com.foroescolar.exceptions.security.filters.token.TokenExpiredException;
import com.foroescolar.services.TokenService;
import com.foroescolar.utils.token.TokenExtractor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@Order(2)
@Slf4j
public class JwtAuthenticationFilter extends BaseSecurityFilter {
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            FilterErrorHandler errorHandler,
            TokenService tokenService,
            UserDetailsService userDetailsService) {
        super(errorHandler);
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(SecurityFilterContext context) throws ServletException, IOException {
        try {
            Optional<String> token = TokenExtractor.extractFromRequest(context.request());

            token.ifPresent(s -> processToken(s, context));
            log.info("Token procesado correctamente{}", token);

            context.filterChain().doFilter(context.request(), context.response());
        } catch (TokenExpiredException ex) {
            log.debug("Token expirado detectado en el filtro JWT", ex);
            SecurityContextHolder.clearContext();
            getErrorHandler().handleTokenExpired(context.request(), context.response(), ex);
            // No propagamos la excepción ni continuamos la cadena de filtros
        }
    }

    private void processToken(String token, SecurityFilterContext context) {
        try {
            String username = tokenService.getUsernameFromToken(token);
            if (username != null) {
                authenticateUser(username, context.request());
                log.info("Usuario autenticado correctamente: {}", username);
            }
        } catch (TokenExpiredException ex) {
            throw ex; // Propagamos la excepción para que sea manejada en el bloque catch superior
        } catch (Exception ex) {
            log.error("Error procesando el token JWT", ex);
            throw new JwtAuthenticationException("Error al procesar el token de autenticación " +  ex);
        }
    }

    private void authenticateUser(String username, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Usuario autenticado correctamente1: {}", userDetails.getAuthorities());
    }
}