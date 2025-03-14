package com.foroescolar.config.security.filters;

import com.foroescolar.exceptions.security.filters.token.TokenInvalidatedException;
import com.foroescolar.exceptions.security.filters.FilterErrorHandler;
import com.foroescolar.services.TokenService;
import com.foroescolar.utils.token.TokenExtractor;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(3)
@Slf4j
public class BlacklistTokenFilter extends BaseSecurityFilter {
    private final TokenService tokenService;

    // Caché local de tokens en lista negra
    private final Set<String> blacklistCache = ConcurrentHashMap.newKeySet();

    // Timestamp de última sincronización
    private volatile long lastSyncTime = 0;

    // Intervalo entre sincronizaciones forzadas (5 minutos)
    private static final long SYNC_INTERVAL = 300000;

    public BlacklistTokenFilter(
            FilterErrorHandler errorHandler,
            TokenService tokenService) {
        super(errorHandler, "/api/auth/login", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**");
        this.tokenService = tokenService;

        // Sincronización inicial
        syncBlacklist();
    }

    @Override
    protected void doFilterInternal(SecurityFilterContext context) throws ServletException, IOException {
        // Verificar si es hora de sincronizar
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSyncTime > SYNC_INTERVAL) {
            syncBlacklist();
        }

        Optional<String> token = TokenExtractor.extractFromRequest(context.request());

        if (token.isPresent()) {
            String tokenValue = token.get();

            // Verificar primero en caché local
            if (blacklistCache.contains(tokenValue)) {
                throw new TokenInvalidatedException("Token ha sido invalidado");
            }

            // Si no está en caché pero ha pasado tiempo, verificar en fuente de datos
            if (currentTime - lastSyncTime > 60000) { // 1 minuto
                if (tokenService.isTokenInBlacklist(tokenValue)) {
                    blacklistCache.add(tokenValue);
                    throw new TokenInvalidatedException("Token ha sido invalidado");
                }
            }

            // Logging menos verboso (solo en niveles de debug)
            if (log.isDebugEnabled()) {
                log.debug("Token verificado contra lista negra: OK");
            }
        }

        context.filterChain().doFilter(context.request(), context.response());
    }

    // Sincronizar caché local con lista negra persistente
    @Scheduled(fixedRate = 300000) // 5 minutos
    public void syncBlacklist() {
        try {
            Set<String> persistentBlacklist = tokenService.getAllBlacklistedTokens();

            // Actualizar caché local
            blacklistCache.clear();
            blacklistCache.addAll(persistentBlacklist);

            lastSyncTime = System.currentTimeMillis();
            log.debug("Lista negra sincronizada, {} tokens en caché", blacklistCache.size());
        } catch (Exception e) {
            log.error("Error sincronizando lista negra de tokens", e);
        }
    }

    // Método para añadir un token a la lista negra (para uso desde el servicio)
    public void addToBlacklist(String token) {
        blacklistCache.add(token);
    }
}