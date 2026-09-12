package com.muneer.store.services;

import com.muneer.store.config.JwtConfig;
import com.muneer.store.entities.User;
import com.muneer.store.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {
    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;

    public String generateAccessToken(String email){
        var user = userRepository.findByEmail(email).orElseThrow(()->
                                            new UsernameNotFoundException("User not found"));

        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public String generateRefreshToken(String email){
        var user = userRepository.findByEmail(email).orElseThrow(()->
                new UsernameNotFoundException("User not found"));

        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    private String generateToken(User user, long tokenExpiration) {
        return Jwts.builder()
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public boolean validateToken(String token){
        try {


            var claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException ex) {
              return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getIdFromToken(String token){
        return Long.valueOf(getClaims(token).getSubject());

    }
}
