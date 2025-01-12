package com.dev.ForoEscolar.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.dev.ForoEscolar.model.User;
import com.dev.ForoEscolar.services.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${api.security.secret}")
    private String apiSecret;

    private final Set<String> blacklistedTokens = Collections.synchronizedSet(new HashSet<>());


    @Override
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            return JWT.create()
                    .withIssuer("Foro Escolar")
                    .withSubject(user.getEmail())
                    .withClaim("id", user.getId())
                    .withClaim("role", user.getRol().getAuthority())
                    .withClaim("nombre", user.getNombre())
                    .withJWTId(UUID.randomUUID().toString())
                    .withIssuedAt(new Date())
                    .withExpiresAt(Date.from(generateExpirationDate()))
                    .sign(algorithm);

        } catch (JWTCreationException e) {
            throw new RuntimeException("Error al generar el token", e);
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Token vacío o nulo");
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            DecodedJWT verifier = JWT.require(algorithm)
                    .withIssuer("Foro Escolar")
                    .build()
                    .verify(token.trim());

            String subject = verifier.getSubject();
            if (subject == null) {
                throw new RuntimeException("Token no contiene subject");
            }

            return subject;
        } catch (TokenExpiredException e) {
            throw new TokenExpiredException("El token expiro en: " , e.getExpiredOn());
        }
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


    @Override
    public void invalidateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            JWT.require(algorithm)
                    .withIssuer("Foro Escolar")
                    .build()
                    .verify(token);

            blacklistedTokens.add(token);
        } catch (Exception e) {
            throw new TokenExpiredException("Error al invalidar el token", null);
        }
    }

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
