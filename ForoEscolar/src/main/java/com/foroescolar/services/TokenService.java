package com.foroescolar.services;

import com.foroescolar.model.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {

    boolean validateToken(String token, UserDetails userDetails);
    String generateToken(User user);
    String getUsernameFromToken(String token);
    void invalidateToken(String token);

    boolean isTokenBlacklisted(String token);
    public boolean isTokenInBlacklist(String token);
}
