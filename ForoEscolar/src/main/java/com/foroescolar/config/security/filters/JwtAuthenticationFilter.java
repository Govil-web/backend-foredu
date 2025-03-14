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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(2)
@Slf4j
public class JwtAuthenticationFilter extends BaseSecurityFilter {
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    // Caché para tokens validados (LRU - máximo 1000 entradas)
    private final Map<String, UserDetails> tokenCache = new LinkedHashMap<>(1000, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, UserDetails> eldest) {
            return size() > 1000;
        }
    };

    // Caché para tokens inválidos (para rechazar rápidamente)
    private final Map<String, Long> invalidTokenCache = new ConcurrentHashMap<>();

    public JwtAuthenticationFilter(
            FilterErrorHandler errorHandler,
            TokenService tokenService,
            UserDetailsService userDetailsService) {
        super(errorHandler, "/api/auth/login", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**");
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(SecurityFilterContext context) throws ServletException, IOException {
        // Verificar si la autenticación ya existe
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            context.filterChain().doFilter(context.request(), context.response());
            return;
        }

        try {
            Optional<String> tokenOpt = TokenExtractor.extractFromRequest(context.request());

            if (tokenOpt.isPresent()) {
                String token = tokenOpt.get();

                // Verificar si el token está en caché de inválidos
                if (invalidTokenCache.containsKey(token)) {
                    log.debug("Token previamente identificado como inválido");
                    context.filterChain().doFilter(context.request(), context.response());
                    return;
                }

                processToken(token, context);
            }

            log.debug("Filtro JWT procesado correctamente");
            context.filterChain().doFilter(context.request(), context.response());
        } catch (TokenExpiredException ex) {
            log.debug("Token expirado detectado en el filtro JWT");
            SecurityContextHolder.clearContext();
            getErrorHandler().handleTokenExpired(context.request(), context.response(), ex);
        }
    }

    private void processToken(String token, SecurityFilterContext context) {
        try {
            // Verificar si el token está en caché
            synchronized (tokenCache) {
                UserDetails cachedUserDetails = tokenCache.get(token);
                if (cachedUserDetails != null) {
                    authenticateWithUserDetails(cachedUserDetails, context.request());
                    log.debug("Usuario autenticado desde caché: {}", cachedUserDetails.getUsername());
                    return;
                }
            }

            // No está en caché, procesar normalmente
            String username = tokenService.getUsernameFromToken(token);
            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Guardar en caché
                synchronized (tokenCache) {
                    tokenCache.put(token, userDetails);
                }

                authenticateWithUserDetails(userDetails, context.request());
                log.info("Usuario autenticado correctamente: {}", username);
            }
        } catch (TokenExpiredException ex) {
            // Guardar en caché de inválidos con timestamp
            invalidTokenCache.put(token, System.currentTimeMillis());
            throw ex;
        } catch (Exception ex) {
            // Guardar en caché de inválidos con timestamp
            invalidTokenCache.put(token, System.currentTimeMillis());
            log.error("Error procesando el token JWT", ex);
            throw new JwtAuthenticationException("Error al procesar el token de autenticación: " + ex.getMessage());
        }
    }

    private void authenticateWithUserDetails(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Usuario autenticado: {}", userDetails.getAuthorities());
    }

    // Limpiar caché de tokens inválidos cada hora (tokens que tienen más de 30 minutos)
    @Scheduled(fixedRate = 3600000)
    public void cleanInvalidTokenCache() {
        long currentTime = System.currentTimeMillis();
        invalidTokenCache.entrySet().removeIf(entry ->
                (currentTime - entry.getValue()) > 1800000); // 30 minutos
        log.debug("Limpieza de caché de tokens inválidos completada");
    }
}