package com.example.demo.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    public static final String SECRET_KEY = "iOjEsImlhdCI6MTc0MjE5ODA2NCwiZXh";
    public static final long EXPIRATION_TIME = 1000 * 60 * 60 ;
    private static final SecretKey SECRET = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    /*
     * 生成token
     * @param email
     * @param userId
     */
    public String generateToken(String email,Long userId){
        return Jwts.builder()
                .setSubject(email)
                .claim("id",userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET,SignatureAlgorithm.HS256)
                .compact();
    }

    /*
     * 解析token
     * @param token
     */
    public Claims extractClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /*
     * 從token中獲取email
     * @param token
     */
    public String getEmailFromToken(String token){
        return extractClaims(token).getSubject();
    }

    /*
     * 從token中獲取userId
     * @param token
     */
    public Long getUserIdFromToken(String token){
        return extractClaims(token).get("id",Long.class);
    }

    /*
     * 驗證token是否過期
     * @param token
     */
    public boolean isTokenExpired(String token){
        return extractClaims(token).getExpiration().before(new Date());
    }
    
}
