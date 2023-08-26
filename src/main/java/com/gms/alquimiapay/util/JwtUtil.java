package com.gms.alquimiapay.util;

import com.gms.alquimiapay.constants.StringValues;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil
{

    @Autowired
    private Environment environment;

    private static final String EMAIL_KEY = "email";

    public String createJWTString(String userEmail){
        return Jwts.builder()
                .claim(EMAIL_KEY, userEmail)
                .setSubject(userEmail)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .signWith(getJWTSigningKey())
                .compact();
    }

    public String getUserEmailFromJWTToken(String token){
        Jws<Claims> claimsJws = getClaimsFromToken(cleanToken(token));
        return (String) claimsJws.getBody().get(EMAIL_KEY);
    }

    public Jws<Claims> getClaimsFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getJWTSigningKey())
                .build()
                .parseClaimsJws(token);
    }
    private Key getJWTSigningKey(){
        String secret = environment.getProperty("gms.security.jwt.secret");

        return new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS256.getJcaName());
    }

    public LocalDateTime getJWTExpiration(LocalDateTime createdDateTime){
        String expirationString = environment.getProperty("gms.security.jwt.expirationInMinutes");
        assert expirationString != null;
        return createdDateTime.plusMinutes(Long.parseLong(expirationString));
    }

    private String cleanToken(String authToken){
        return authToken.startsWith(StringValues.AUTH_HEADER_BEARER_KEY) ? authToken.replace(StringValues.AUTH_HEADER_BEARER_KEY, StringValues.EMPTY_STRING).trim() : authToken.trim();
    }
}
