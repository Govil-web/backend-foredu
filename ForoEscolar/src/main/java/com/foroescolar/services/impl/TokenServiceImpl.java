package com.foroescolar.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.foroescolar.exceptions.security.ErrorCode;
import com.foroescolar.exceptions.security.filters.token.*;
import com.foroescolar.model.User;
import com.foroescolar.services.TokenService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import io.lettuce.core.RedisConnectionException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    @Value("${api.security.secret}")
    private String apiSecret;

    private final Set<String> blacklistedTokens = Collections.synchronizedSet(new HashSet<>());

    //private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_BLACKLIST_PREFIX = "blacklist:";
    private static final long TOKEN_BLACKLIST_DURATION = 24;
    private static final String FORO_ESCOLAR = "Foro Escolar";

//    public TokenServiceImpl(RedisTemplate<String, String> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }


    @Override
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            return JWT.create()
                    .withIssuer(FORO_ESCOLAR)
                    .withSubject(user.getEmail())
                    .withClaim("id", user.getId())
                    .withClaim("role", user.getRol().getAuthority())
                    .withClaim("nombre", user.getNombre())
                    .withJWTId(UUID.randomUUID().toString())
                    .withIssuedAt(new Date())
                    .withExpiresAt(Date.from(generateExpirationDate()))
                    .sign(algorithm);

        } catch (JWTCreationException e) {
            throw new TokenMalformedException("Error al generar el token" +  e.getMessage());
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        validateTokenNotEmpty(token);

        try {
            DecodedJWT decodedJWT = verifyToken(token.trim());
            return extractSubject(decodedJWT);
        } catch (JWTVerificationException e) {
            log.error("Error al verificar token: {}", e.getMessage());
            throw new TokenExpiredException("El token ha expirado", getExpirationDateFromToken(token));
        } catch (Exception e) {
            log.error("Error inesperado al procesar token: {}", e.getMessage());
            throw new TokenException("Error al procesar el token", ErrorCode.TOKEN_INVALID);
        }
    }

    private void validateTokenNotEmpty(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new TokenException("Token vacío o nulo", ErrorCode.TOKEN_INVALID);
        }
    }

    private DecodedJWT verifyToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(apiSecret);
        return JWT.require(algorithm)
                .withIssuer(FORO_ESCOLAR)
                .build()
                .verify(token);
    }

    private String extractSubject(DecodedJWT jwt) {
        String subject = jwt.getSubject();
        if (subject == null) {
            throw new TokenException("Token no contiene subject", ErrorCode.TOKEN_INVALID);
        }
        return subject;
    }



    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        if (blacklistedTokens.contains(token)) {
            return false;
        }

        try {
            final String username = getUsernameFromToken(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

//    @Override
//    public void invalidateToken(String token) {
//        log.info("Iniciando proceso de invalidación de token");
//        try {
//            validateTokenBeforeBlacklisting(token);
//            addTokenToBlacklist(token);
//            log.info("Token invalidado exitosamente");
//        } catch (JWTVerificationException e) {
//            log.error("Error al verificar el token antes de invalidar: {}", e.getMessage());
//            throw new TokenInvalidException("El token proporcionado no es válido");
//        } catch (RedisConnectionException e) {
//            log.error("Error de conexión con Redis al invalidar token: {}", e.getMessage());
//            throw new TokenOperationException("Error al procesar la invalidación del token", e);
//        } catch (Exception e) {
//            log.error("Error inesperado al invalidar token: {}", e.getMessage(), e);
//            throw new TokenOperationException("Error inesperado al invalidar el token", e);
//        }
//    }

    private void validateTokenBeforeBlacklisting(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(apiSecret);
        JWT.require(algorithm)
                .withIssuer(FORO_ESCOLAR)
                .build()
                .verify(token);
    }

//    private void addTokenToBlacklist(String token) {
//        redisTemplate.opsForValue().set(
//                TOKEN_BLACKLIST_PREFIX + token,
//                "true",
//                TOKEN_BLACKLIST_DURATION,
//                TimeUnit.HOURS
//        );
//    }

//    @Override
//    public boolean isTokenInBlacklist(String token) {
//        Boolean result = redisTemplate.hasKey(TOKEN_BLACKLIST_PREFIX + token);
//        return Boolean.TRUE.equals(result);
//    }


    @Override
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    private boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private Date getExpirationDateFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token.trim());
        return jwt.getExpiresAt();
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now()
                .plusHours(24)
                .toInstant(ZoneOffset.of("-05:00"));
    }
}
