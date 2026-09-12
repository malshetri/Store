package com.muneer.store.services;

import com.muneer.store.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    private final UserRepository userRepository;
    @Value("${spring.jwt.secret}")
    private String secret;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(String email){
        var user = userRepository.findByEmail(email).orElseThrow(()->
                                            new UsernameNotFoundException("User not found"));

        final long tokenExpiration = 86400; // a day in seconds
        return  Jwts.builder()
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
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
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getIdFromToken(String token){
        return Long.valueOf(getClaims(token).getSubject());

    }
}
